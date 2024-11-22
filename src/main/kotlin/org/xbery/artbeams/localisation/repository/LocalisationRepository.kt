package org.xbery.artbeams.localisation.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.repository.keyvalue.CachedKeyValueRepository
import org.xbery.artbeams.jooq.schema.tables.references.LOCALISATION

/**
 * Stores key-value pairs for application localisation.
 *
 * @author Radek Beran
 */
@Repository
class LocalisationRepository(val dsl: DSLContext) : CachedKeyValueRepository() {
    override fun findAllEntries(): Map<String, String> {
        return dsl.selectFrom(LOCALISATION).fetch { c ->
            requireNotNull(c.entryKey) to requireNotNull(c.entryValue)
        }.toMap()
    }
}
