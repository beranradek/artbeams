package org.xbery.artbeams.config.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.config.domain.Config
import org.xbery.artbeams.config.repository.mapper.ConfigMapper
import org.xbery.artbeams.config.repository.mapper.ConfigUnmapper
import org.xbery.artbeams.jooq.schema.tables.references.CONFIG

/**
 * Repository for CRUD operations on config entries.
 *
 * @author Radek Beran
 */
@Repository
class ConfigRepository(
    val dsl: DSLContext,
    private val mapper: ConfigMapper,
    private val unmapper: ConfigUnmapper
) {

    fun findConfigs(pagination: Pagination): ResultPage<Config> {
        val totalCount = dsl.selectCount()
            .from(CONFIG)
            .fetchOne(0, Long::class.java) ?: 0L

        val records = dsl.selectFrom(CONFIG)
            .orderBy(CONFIG.ENTRY_KEY)
            .limit(pagination.limit)
            .offset(pagination.offset)
            .fetch(mapper)

        return ResultPage(records, pagination.withTotalCount(totalCount))
    }

    fun findByKey(entryKey: String): Config? {
        return dsl.selectFrom(CONFIG)
            .where(CONFIG.ENTRY_KEY.eq(entryKey))
            .fetchOne(mapper)
    }

    fun create(config: Config): Config {
        val record = unmapper.unmap(config)
        dsl.insertInto(CONFIG)
            .set(record)
            .execute()
        return requireByKey(config.entryKey)
    }

    fun update(originalKey: String, config: Config): Config {
        val record = unmapper.unmap(config)
        val updatedCount = dsl.update(CONFIG)
            .set(record)
            .where(CONFIG.ENTRY_KEY.eq(originalKey))
            .execute()
        when {
            updatedCount == 0 -> error("Config not updated")
            updatedCount > 1 -> error("More than one config was updated")
        }
        return requireByKey(config.entryKey)
    }

    fun deleteByKey(entryKey: String): Boolean {
        return dsl.deleteFrom(CONFIG)
            .where(CONFIG.ENTRY_KEY.eq(entryKey))
            .execute() > 0
    }

    fun requireByKey(entryKey: String): Config {
        return findByKey(entryKey) ?: error("Config with key $entryKey not found")
    }
}
