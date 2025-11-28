package org.xbery.artbeams.localisation.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.localisation.domain.EditedLocalisation
import org.xbery.artbeams.localisation.domain.Localisation
import org.xbery.artbeams.localisation.repository.LocalisationRepository

/**
 * @author Radek Beran
 */
@Service
open class LocalisationServiceImpl(
    private val localisationRepository: LocalisationRepository
) : LocalisationService {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun findLocalisations(pagination: Pagination): ResultPage<Localisation> {
        logger.info("Finding localisations with pagination: offset=${pagination.offset}, limit=${pagination.limit}")
        return localisationRepository.findLocalisations(pagination)
    }

    override fun findByKey(entryKey: String): Localisation? {
        return localisationRepository.findByKey(entryKey)
    }

    override fun saveLocalisation(edited: EditedLocalisation): Localisation {
        return try {
            val localisation = Localisation(edited.entryKey, edited.entryValue)
            val savedLocalisation = if (edited.originalKey.isEmpty()) {
                // New localisation
                localisationRepository.create(localisation)
            } else {
                // Update existing localisation
                localisationRepository.update(edited.originalKey, localisation)
            }
            // Reload cache after save
            localisationRepository.reloadEntries()
            savedLocalisation
        } catch (ex: Exception) {
            logger.error("Save of localisation ${edited.entryKey} finished with error ${ex.message}", ex)
            throw ex
        }
    }

    override fun deleteLocalisation(entryKey: String): Boolean {
        return try {
            val result = localisationRepository.deleteByKey(entryKey)
            // Reload cache after delete
            localisationRepository.reloadEntries()
            result
        } catch (ex: Exception) {
            logger.error("Delete of localisation $entryKey finished with error ${ex.message}", ex)
            throw ex
        }
    }
}
