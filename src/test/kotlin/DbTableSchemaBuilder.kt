import org.xbery.artbeams.common.access.repository.EntityAccessCountMapper
import org.xbery.overview.mapper.EntityMapper
import org.xbery.overview.sql.mapper.MySqlSchemaBuilder

/**
 * Generates SQL schema for DB table (given mapper).
 * MySQL is supported for now.
 * @author Radek Beran
 */
object DbTableSchemaBuilder {
    fun main(
        @Suppress("Unused", "UNUSED_PARAMETER")
        args: Array<String>
    ) {
        println(generateSchema(EntityAccessCountMapper.Instance))
    }

    private fun <T, F> generateSchema(entityMapper: EntityMapper<T, F>): String {
        return MySqlSchemaBuilder().composeCreateTableSQL<T>(entityMapper.tableName, entityMapper.attributes)
    }
}