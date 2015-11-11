package com.hannesdorfmann.data.news

import com.hannesdorfmann.data.pager.Pager
import com.hannesdorfmann.data.source.Source
import io.plaidapp.data.PlaidItem

/**
 *
 * A factory responsible to create a [Pager] instance
 * @author Hannes Dorfmann
 */
interface PagerFactory<I> {

    /**
     * Create a [Pager] for a given [Source]
     */
    fun createPager(source: Source): Pager<I, List<PlaidItem>>
}