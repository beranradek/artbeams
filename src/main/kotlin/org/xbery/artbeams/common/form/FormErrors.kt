package org.xbery.artbeams.common.form

import net.formio.FormData
import net.formio.validation.ConstraintViolationMessage
import net.formio.validation.Severity
import net.formio.validation.ValidationResult

/**
 * @author Radek Beran
 */
object FormErrors {

    /**
     * Returns form data with error message about invalid captcha token.
     */
    fun <T> formDataWithCaptchaInvalidError(formData: FormData<T>): FormData<T> {
        val errorMessage = ConstraintViolationMessage(
            Severity.WARNING,
            "Captcha token was incorrect.",
            "captcha.invalid",
            mapOf()
        )
        return FormData(
            formData.data,
            ValidationResult(
                formData.validationResult.fieldMessages,
                listOf(errorMessage) + formData.validationResult.globalMessages
            )
        )
    }

    /**
     * Returns form data with error message about internal error during form processing.
     */
    fun <T> formDataWithInternalError(formData: FormData<T>): FormData<T> {
        val errorMessage = ConstraintViolationMessage(
            Severity.ERROR,
            "Internal error while processing the form.",
            "form-processing.error",
            mapOf()
        )
        return FormData(
            formData.data,
            ValidationResult(
                formData.validationResult.fieldMessages,
                listOf(errorMessage) + formData.validationResult.globalMessages
            )
        )
    }
}