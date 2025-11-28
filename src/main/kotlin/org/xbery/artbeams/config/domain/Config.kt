package org.xbery.artbeams.config.domain

/**
 * Config entry entity
 * @author Radek Beran
 */
data class Config(
    val entryKey: String,
    val entryValue: String
) {
    fun updatedWith(edited: EditedConfig): Config {
        return this.copy(
            entryKey = edited.entryKey,
            entryValue = edited.entryValue
        )
    }

    fun toEdited(): EditedConfig {
        return EditedConfig(
            originalKey = this.entryKey,
            entryKey = this.entryKey,
            entryValue = this.entryValue
        )
    }

    companion object {
        val Empty: Config = Config("", "")
    }
}
