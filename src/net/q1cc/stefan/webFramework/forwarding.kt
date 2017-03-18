package net.q1cc.stefan.webFramework

class RerouteException(val path : String ) : RuntimeException()
inline fun reroute(path:String): Nothing = throw RerouteException(path)

class RedirectException(val path : String ) : RuntimeException()
inline fun redirect(path:String): Nothing = throw RedirectException(path)