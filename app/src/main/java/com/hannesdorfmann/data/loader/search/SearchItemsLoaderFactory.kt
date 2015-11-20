package com.hannesdorfmann.data.loader.search

import com.hannesdorfmann.data.loader.router.RouteCallerFactory
import com.hannesdorfmann.data.loader.router.Router
import com.hannesdorfmann.data.news.ItemsLoader
import io.plaidapp.data.PlaidItem
import io.plaidapp.data.api.designernews.DesignerNewsService
import io.plaidapp.data.api.dribbble.DribbbleSearch
import javax.inject.Inject

/**
 * A factory that creates a [ItemsLoader] for a given search query string
 *
 * @author Hannes Dorfmann
 */
class SearchItemsLoaderFactory @Inject constructor(private val designerNewsBackend: DesignerNewsService) {


    /**
     * Create a new  [ItemsLoader] for the given search query
     */
    fun create(searchQuery: String): ItemsLoader<List<PlaidItem>> {

        val routes = arrayListOf(
                DribbbleSearchCallerFactory(searchQuery, DribbbleSearch.SORT_POPULAR),
                DesignerNewsSearchCallerFactory(searchQuery, designerNewsBackend)
        )

        val router = Router(routes)

        return ItemsLoader(router)
    }

}