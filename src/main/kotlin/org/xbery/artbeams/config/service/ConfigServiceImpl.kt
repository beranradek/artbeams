package org.xbery.artbeams.config.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.config.domain.Config
import org.xbery.artbeams.config.domain.EditedConfig
import org.xbery.artbeams.config.repository.AppConfig
import org.xbery.artbeams.config.repository.ConfigRepository

/**
 * @author Radek Beran
 */
@Service
open class ConfigServiceImpl(
    private val configRepository: ConfigRepository,
    private val appConfig: AppConfig
) : ConfigService {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)
    
    private val sensitiveKeyPatterns = listOf(
        "secret", "secretkey", "password", "salt", "token", 
        "google.oauth.client.json", "api.key", "apikey",
        "client.secret", "private.key", "privatekey"
    )
    
    private fun isSensitiveKey(key: String): Boolean {
        val lowerKey = key.lowercase()
        return sensitiveKeyPatterns.any { pattern ->
            lowerKey.contains(pattern.lowercase())
        }
    }

    override fun findConfigs(pagination: Pagination, search: String?): ResultPage<Config> {
        logger.info("Finding configs with pagination: offset=${pagination.offset}, limit=${pagination.limit}, search=$search")
        val resultPage = configRepository.findConfigs(pagination, search)
        
        // Mask sensitive values for admin display
        val maskedConfigs = resultPage.records.map { config ->
            if (isSensitiveKey(config.entryKey)) {
                config.copy(entryValue = "*****")
            } else {
                config
            }
        }
        
        return ResultPage(maskedConfigs, resultPage.pagination)
    }

    override fun findByKey(entryKey: String): Config? {
        val config = configRepository.findByKey(entryKey)
        return config?.let { 
            if (isSensitiveKey(it.entryKey)) {
                it.copy(entryValue = "*****")
            } else {
                it
            }
        }
    }

    override fun saveConfig(edited: EditedConfig): Config {
        return try {
            val config = Config(edited.entryKey, edited.entryValue)
            val savedConfig = if (edited.originalKey.isEmpty()) {
                // New config
                configRepository.create(config)
            } else {
                // Update existing config
                configRepository.update(edited.originalKey, config)
            }
            // Reload cache after save
            appConfig.reloadConfigEntries()
            savedConfig
        } catch (ex: Exception) {
            logger.error("Save of config ${edited.entryKey} finished with error ${ex.message}", ex)
            throw ex
        }
    }

    override fun deleteConfig(entryKey: String): Boolean {
        return try {
            val result = configRepository.deleteByKey(entryKey)
            // Reload cache after delete
            appConfig.reloadConfigEntries()
            result
        } catch (ex: Exception) {
            logger.error("Delete of config $entryKey finished with error ${ex.message}", ex)
            throw ex
        }
    }
}
