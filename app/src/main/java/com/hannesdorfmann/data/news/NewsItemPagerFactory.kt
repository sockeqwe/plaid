package com.hannesdorfmann.data.news

import android.support.v4.util.SparseArrayCompat
import com.hannesdorfmann.data.backend.BackendManager
import com.hannesdorfmann.data.backend.paging.BackendPagingFunc
import com.hannesdorfmann.data.pager.Pager
import com.hannesdorfmann.data.source.Source
import io.plaidapp.data.PlaidItem

/**
 *
 *
 * @author Hannes Dorfmann
 */
class NewsItemPagerFactory() {

    private val pagerFactories = SparseArrayCompat<PagerFactory<Any>>()

    fun create(source: Source): Pager<Any, List<PlaidItem>> {

        // Throws an exception if no factory registered for this pager --> factor == null
        val pagerFactory = pagerFactories.get(source.backendId)!!
        return pagerFactory.createPager(source)
    }

    /**
     * Registers a factory for a given backend
     */
    fun addFactory(backendId: Int, factory: PagerFactory<Any>): NewsItemPagerFactory {

        if (pagerFactories.get(backendId) != null) {
            throw IllegalArgumentException("A ${PagerFactory::class.simpleName} is already registered for backend with id 0 ${backendId}")
        }
        pagerFactories.put(backendId, factory)
        return this
    }
}