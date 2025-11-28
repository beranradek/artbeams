package org.xbery.artbeams.localisation.domain

/**
 * Localisation entry entity
 * @author Radek Beran
 */
data class Localisation(
    val entryKey: String,
    val entryValue: String
) {
    fun updatedWith(edited: EditedLocalisation): Localisation {
        return this.copy(
            entryKey = edited.entryKey,
            entryValue = edited.entryValue
        )
    }

    fun toEdited(): EditedLocalisation {
        return EditedLocalisation(
            originalKey = this.entryKey,
            entryKey = this.entryKey,
            entryValue = this.entryValue
        )
    }

    companion object {
        val Empty: Localisation = Localisation("", "")
    }
}
