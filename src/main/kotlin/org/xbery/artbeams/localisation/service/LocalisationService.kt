package org.xbery.artbeams.localisation.service

import org.xbery.artbeams.common.overview.Pagination
import org.xbery.artbeams.common.overview.ResultPage
import org.xbery.artbeams.localisation.domain.EditedLocalisation
import org.xbery.artbeams.localisation.domain.Localisation

/**
 * @author Radek Beran
 */
interface LocalisationService {
    fun findLocalisations(pagination: Pagination, search: String? = null): ResultPage<Localisation>
    fun findByKey(entryKey: String): Localisation?
    fun saveLocalisation(edited: EditedLocalisation): Localisation
    fun deleteLocalisation(entryKey: String): Boolean
}
