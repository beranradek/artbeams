import org.xbery.artbeams.common.access.repository.EntityAccessCountMapper
import org.xbery.overview.mapper.EntityMapper
import org.xbery.overview.sql.mapper.MySqlSchemaBuilder

/**
  * Generates SQL schema for DB table (given mapper).
  * MySQL is supported for now.
  * @author Radek Beran
  */
object DbTableSchemaBuilder {

  def main(args: Array[String]): Unit = {
    println(generateSchema(EntityAccessCountMapper.Instance))
  }

  def generateSchema[T, F](entityMapper: EntityMapper[T, F]): String = {
    new MySqlSchemaBuilder().composeCreateTableSQL[T](entityMapper.getTableName(), entityMapper.getAttributes())
  }
}
