package org.xbery.artbeams.common.form

import net.formio.AbstractRequestParams
import net.formio.upload.RequestProcessingError
import net.formio.upload.UploadedFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import javax.servlet.http.HttpServletRequest

/**
 * Form request params for Spring web.
 *
 * @author Radek Beran
 */
class SpringHttpServletRequestParams(val request: HttpServletRequest) : AbstractRequestParams() {
    override fun getParamNames(): Iterable<String> = request.parameterNames.toList()

    override fun getParamValues(paramName: String?): Array<String> {
        val values = request.getParameterValues(paramName)
        return values ?: arrayOf()
    }

    override fun getUploadedFiles(paramName: String?): Array<UploadedFile> {
        if (paramName == null) {
            return arrayOf()
        }
        if (request is MultipartHttpServletRequest) {
            return request.getFiles(paramName).map { file ->
                SpringUploadedFile(file.originalFilename, file.contentType, file.size, file)
            }.toTypedArray()
        }
        return arrayOf()
    }

    override fun getRequestError(): RequestProcessingError? {
        return null // not supported, handled by Spring web upload
    }
}
