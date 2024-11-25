package org.xbery.artbeams.sequences.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.xbery.artbeams.jooq.schema.tables.references.SEQUENCES

/**
 * @author Radek Beran
 */
@Repository
class SequenceRepository(
    private val dsl: DSLContext
) {

    fun getAndIncrementNextSequenceValue(sequenceName: String): Long {
        return dsl.update(SEQUENCES)
            .set(SEQUENCES.NEXT_VALUE, SEQUENCES.NEXT_VALUE.plus(1))
            .where(SEQUENCES.SEQUENCE_NAME.eq(sequenceName))
            .returning(SEQUENCES.NEXT_VALUE)
            .fetchOne()
            ?.getValue(SEQUENCES.NEXT_VALUE) ?: throw IllegalStateException("Sequence $sequenceName not found")
    }
}
