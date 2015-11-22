package com.hannesdorfmann.data.loader.search

import com.hannesdorfmann.data.loader.router.RouteCaller
import com.hannesdorfmann.data.loader.router.RouteCallerFactory
import io.plaidapp.data.PlaidItem
import io.plaidapp.data.api.designernews.DesignerNewsService
import io.plaidapp.data.api.designernews.model.StoriesResponse
import io.plaidapp.data.api.dribbble.DribbbleSearch
import rx.Observable

/**
 *
 *
 * @author Hannes Dorfmann
 */
class DesignerNewsSearchCallerFactory(private val searchQuery: String, private val backend: DesignerNewsService) : RouteCallerFactory<List<PlaidItem>> {

    val extractPlaidItemsFromStory = fun(story: StoriesResponse): List<PlaidItem> {
        return story.stories
    }

    val searchCall = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.search(searchQuery, pageOffset).map(extractPlaidItemsFromStory)
    }

    private val callers = arrayListOf(RouteCaller(0, 100, searchCall))

    override fun getAllBackendCallers(): Observable<List<RouteCaller<List<PlaidItem>>>> {
        return Observable.just(callers)
    }
}