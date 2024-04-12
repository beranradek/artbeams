package org.xbery.artbeams.common.repository

import org.jooq.UpdatableRecord

/**
 * Minimalistic abstract class for storing data to database. It can serve as a base
 * for all abstract or concrete implementations.
 *
 * @author Radek Beran
 */
abstract class AbstractRecordStorage<R : UpdatableRecord<R>> {

    open fun create(record: R): Int = record.store()

    open fun update(record: R): Int = record.update()

    open fun delete(record: R): Int = record.delete()
}
