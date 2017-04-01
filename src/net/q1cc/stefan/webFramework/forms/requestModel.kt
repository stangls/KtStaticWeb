package net.q1cc.stefan.webFramework.forms

import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.web.RoutingContext
import net.q1cc.stefan.webFramework.dslHelpers.noExc
import net.q1cc.stefan.webFramework.dslHelpers.printExc
import net.q1cc.stefan.webFramework.forms.validation.getAdditionalButtons
import net.q1cc.stefan.webFramework.forms.validation.getRuleLists
import net.q1cc.stefan.webFramework.forms.validation.getValidationRules
import net.q1cc.stefan.webFramework.mvp.IModel
import java.io.File
import java.lang.reflect.InvocationTargetException

fun parseRequestModel(context: RoutingContext) : Any? {

    try {

        val params = context.request().params()

        val requestModelClass = Class.forName((params["_requestModelClass"] ?: return null) as String)

        // create new request model. this is the one to be filled
        val requestModel = requestModelClass.newInstance() ?: return null

        // fill its ValidationRules
        requestModel.getValidationRules().forEach {
            val name = it.first
            val validationRule = it.second
            val value = params[name]
            if (value == null) {
                // if it is a fileupload...
                val uploadedFile = context.fileUploads().firstOrNull { it.name() == validationRule.canonicalName(requestModel) }
                if (uploadedFile != null) {
                    printExc {
                        val tempFile = File(uploadedFile.uploadedFileName())
                        val storedFile = File(tempFile.parent+"/"+uploadedFile.fileName())
                        validationRule.value = if (tempFile.renameTo(storedFile)) storedFile else tempFile
                    }
                }
            }else{
                printExc {
                    validationRule.value = value
                }
            }
        }

        // fill its multirules
        requestModel.getRuleLists().forEach {
            val name = it.first
            val multiRule = it.second
            // get values passed in order
            // add values passed with index
            val nameRegex = "^$name\\[.*\\]$".toRegex()
            val values = params.names()
                .filter { it.matches(nameRegex) }
                .groupBy({it.removePrefix("$name[").removeSuffix("]")},{params[it]})
            values.forEach{
                val (idx, value) = it
                if (value.size>0) {
                    printExc {
                        multiRule.getOrCreate(idx).value = value.first()
                    }
                }
            }
            // TODO: also handle file uploads
        }

        // check clicked button
        requestModel.getAdditionalButtons().forEach {
            val name = it.first
            if (params.contains(name)){
                it.second.clicked = true
            }
        }

        return requestModel

    } catch(e: Exception) {
        // it should be ok to ignore it
        e.printStackTrace()
    }

    return null
}

fun <TSessionData : Any> callRequestModelMethod(model: Any, instance: Any, request: HttpServerRequest, sessionData: TSessionData): Boolean {

    try {

        // TODO: find method in model which can handle requestModelClass
        val methods = model.javaClass.declaredMethods
                /*.filter {
                it.isAccessible
            }*/.filter {
            with(it.parameters) {
                // todo: implement or use java/kotlin logic to find appropriate method
                if (size >= 1) {
                    // first parameter must accept the request-model
                    if (!get(0).type.isAssignableFrom(instance.javaClass)) {
                        return@with false
                    }
                    // second parameter may be either session-data
                    // or 2nd & 3rd may be request & session-data
                    if (size >= 2) {
                        val type1=get(1).type
                        if (type1.isAssignableFrom(sessionData.javaClass)) {
                            return@with size==2
                        }
                        if (type1.isAssignableFrom(request.javaClass)) {
                            if (size>=3){
                                return@with get(2).type.isAssignableFrom(sessionData.javaClass)
                            }
                        }
                        return@with false
                    }
                    return@with true
                }
                false
            }
        }

        val method = methods.firstOrNull()
        if (method == null) {
            return false
        }
        when(method.parameterCount) {
            1 -> method.invoke(model, instance)
            2 -> method.invoke(model, instance, sessionData)
            3 -> method.invoke(model, instance, request, sessionData)
        }
        return true

    }catch(e:InvocationTargetException){
        throw e.targetException
        return true
    }catch(e:Exception){
        e.printStackTrace()
        return false
    }

}

fun <TSessionData : Any> handleRequestModelAsForm(requestModel: Any, request: HttpServerRequest, sessionData: TSessionData): Boolean {
    if (requestModel is Form<*>) {
        FormRegistry.set(requestModel)
        val rH = requestModel.requestHandler
        if (rH != null) {
            return callRequestModelMethod(rH, requestModel, request, sessionData)
        }
    }
    return false
}