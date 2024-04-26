package org.xbery.artbeams.common.auth.repository

import org.jooq.DSLContext
import org.jooq.Table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.auth.domain.AuthorizationCode
import org.xbery.artbeams.common.repository.AbstractRecordFetcher
import org.xbery.artbeams.common.repository.AbstractRecordStorage
import org.xbery.artbeams.jooq.schema.tables.records.AuthCodeRecord
import org.xbery.artbeams.jooq.schema.tables.references.AUTH_CODE

/**
 * Implementation of [AuthorizationCodeRepository] that uses JOOQ.
 *
 * @author Radek Beran
 */
@Repository
class SqlAuthorizationCodeRepository(override val dsl: DSLContext) :
    AbstractRecordFetcher<AuthCodeRecord>,
    AbstractRecordStorage<AuthCodeRecord>,
    AuthorizationCodeRepository {

    override val table: Table<AuthCodeRecord> = AUTH_CODE

    override fun createCode(code: AuthorizationCode) {
        create(code)
    }

    override fun updateCode(code: AuthorizationCode) {
        updateBy(code, AUTH_CODE.CODE, code.code)
    }

    override fun findByCodePurposeAndUserId(code: String, purpose: String, userId: String): AuthorizationCode? =
        dsl.selectFrom(table).where(
            AUTH_CODE.CODE.eq(code),
            AUTH_CODE.PURPOSE.eq(purpose),
            AUTH_CODE.USER_ID.eq(userId)
        ).fetchOne()?.into(AuthorizationCode::class.java)
}
