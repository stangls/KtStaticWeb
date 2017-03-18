package net.q1cc.stefan.webFramework.ext

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by stefan on 11.11.16.
 */

fun File.getDirectoryWhichContains(f: File) =
        getDirectoryWhichContains(f.canonicalFile,f.canonicalFile)

private tailrec fun File.getDirectoryWhichContains(f: File, prev: File) : File? {
    return if (f == this) prev
    else getDirectoryWhichContains( f.parentFile ?: return null, f )
}

tailrec fun File.contains(f: File) : Boolean {
    return if (f == this) true
    else contains( f.parentFile ?: return false )
}

fun File.move(destination: String) : File {
    Files.move(Paths.get(absolutePath), Paths.get(destination))
    return File(destination)
}
