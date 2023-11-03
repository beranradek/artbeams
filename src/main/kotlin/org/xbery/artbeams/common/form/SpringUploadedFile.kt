package org.xbery.artbeams.common.form

import net.formio.upload.AbstractUploadedFile
import net.formio.upload.UploadedFile
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel

/**
 * Implementation of [UploadedFile] that uses Spring [MultipartFile].
 *
 * @author Radek Beran
 */
class SpringUploadedFile(fileName: String?, contentType: String?, size: Long, private val multipartFile: MultipartFile) :
    AbstractUploadedFile(fileName, contentType, size) {

    @Throws(IOException::class)
    override fun getContent(): ReadableByteChannel {
        return Channels.newChannel(multipartFile.inputStream)
    }

    /**
     * @see java.lang.Object.finalize
     */
    @Throws(Throwable::class)
    protected fun finalize() {
        deleteTempFile()
    }

    override fun deleteTempFile() {
        // not supported, left to Spring framework
    }

    override fun toString(): String {
        return "File $fileName, size=$size, type=$contentType"
    }

    companion object {
        private const val serialVersionUID = 4928481456790370482L
    }
}
