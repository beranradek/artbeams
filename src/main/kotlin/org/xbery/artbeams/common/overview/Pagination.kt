package org.xbery.artbeams.common.overview

/**
 * Pagination settings with offset, limit and total count of records.
 *
 * @author Radek Beran
 */
data class Pagination(
    val offset: Int,
    val limit: Int,
    /** Total count which can be returned from the server/DB. */
    val totalCount: Long? = null
) {
    /** Returns new pagination with the given updated total count. */
    fun withTotalCount(count: Long): Pagination {
        return this.copy(totalCount = count)
    }

    /** Returns new pagination with the given updated offset. */
    fun withOffset(offset: Int): Pagination {
        return this.copy(offset = offset)
    }

    /** Offset for next page. */
    val nextOffset: Int = this.offset + this.limit

    /** Offset for previous page. */
    val previousOffset: Int
        get() {
            var prevOffset = this.offset - this.limit
            if (prevOffset < 0) {
                prevOffset = 0
            }

            return prevOffset
        }

    fun isFirstPage(): Boolean {
        return this.offset <= 0
    }

    fun isLastPage(): Boolean {
        var next = false
        if (this.totalCount != null) {
            next = this.nextOffset < this.totalCount
        }

        return !next
    }

    /** Current page number starting from 1. */
    val page: Int
        get() {
            return this.offset / this.limit + 1
        }

    /** Total count of pages (available if total count is available). */
    val pageCount: Long?
        get() {
            var cnt: Long? = null
            if (this.totalCount != null) {
                val tc = this.totalCount
                cnt = tc / this.limit + (if (tc % this.limit > 0) 1 else 0)
            }

            return cnt
        }

    override fun toString(): String {
        return "Pagination [offset=" + this.offset + ", limit=" + this.limit + ", totalCount=" + this.totalCount + "]"
    }
}
