package org.xbery.artbeams.common.form

import net.formio.AbstractRequestParams
import net.formio.upload.RequestProcessingError
import net.formio.upload.UploadedFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import jakarta.servlet.http.HttpServletRequest

/**
 * Form request params for Spring web.
 *
 * @author Radek Beran
 */
class SpringHttpServletRequestParams(val request: HttpServletRequest) : AbstractRequestParams() {
    override fun getParamNames(): Iterable<String> = request.parameterNames.toList()

    override fun getParamValues(paramName: String?): Array<String> {
        val values = request.getParameterValues(paramName)
        if (values == null) {
            return arrayOf()
        }
        // For checkboxes with hidden fallback fields, take the last value
        // (hidden field sends "false", checkbox sends "true" when checked)
        if (values.size > 1 && values.all { it == "true" || it == "false" }) {
            return arrayOf(values.last())
        }
        return values
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
