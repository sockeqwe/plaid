package com.hannesdorfmann.data.loader.search

import com.hannesdorfmann.data.backend.paging.HomeDribbbleBackendCallFactory
import com.hannesdorfmann.data.loader.router.RouteCaller
import com.hannesdorfmann.data.loader.router.RouteCallerFactory
import io.plaidapp.data.PlaidItem
import io.plaidapp.data.api.dribbble.DribbbleSearch
import io.plaidapp.data.api.dribbble.DribbbleService
import rx.Observable

/**
 *
 *
 * @author Hannes Dorfmann
 */
class DribbbleSearchCallerFactory(private val searchQurey: String, @DribbbleSearch.SortOrder private val searchOrder: String) : RouteCallerFactory<List<PlaidItem>> {

    val search = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return checkedDefer {
            Observable.just(DribbbleSearch.search(searchQurey, searchOrder, pageOffset) as List<PlaidItem>)
        }
    }

    private val caller = RouteCaller<List<PlaidItem>>(0, 100, search)
    private val callers = arrayListOf(caller)

    override fun getAllBackendCallers(): Observable<List<RouteCaller<List<PlaidItem>>>> {
        return Observable.just(callers)
    }

}