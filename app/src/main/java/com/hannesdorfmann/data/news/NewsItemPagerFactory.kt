package com.hannesdorfmann.data.news

import com.hannesdorfmann.data.backend.BackendManager
import com.hannesdorfmann.data.pager.Pager
import com.hannesdorfmann.data.source.Source
import io.plaidapp.data.PlaidItem

/**
 *
 *
 * @author Hannes Dorfmann
 */
class NewsItemPagerFactory(private val backendManager: BackendManager) {

    fun create(source: Source): Pager<PlaidItem, PlaidItem> {
        throw UnsupportedOperationException("Not implemented yet")
    }

}