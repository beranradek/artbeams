package org.xbery.artbeams.config

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.jooq.impl.DefaultExecuteListenerProvider
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jooq.ExceptionTranslatorExecuteListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

/**
 * @author Radek Beran
 */
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
class DatabaseConfiguration(private val dataSource: DataSource) {

    @Bean
    fun connectionProvider(): DataSourceConnectionProvider =
        DataSourceConnectionProvider(TransactionAwareDataSourceProxy(dataSource))

    @Bean
    fun dslContext(): DSLContext {
        return DefaultDSLContext(configuration())
    }

    fun configuration(): DefaultConfiguration {
        val jooqConfiguration = DefaultConfiguration()
        jooqConfiguration.setSQLDialect(getSQLDialect())
        jooqConfiguration.set(connectionProvider())
        jooqConfiguration
            .set(DefaultExecuteListenerProvider(ExceptionTranslatorExecuteListener.DEFAULT))

        return jooqConfiguration
    }

    protected open fun getSQLDialect(): SQLDialect = SQLDialect.POSTGRES
}
