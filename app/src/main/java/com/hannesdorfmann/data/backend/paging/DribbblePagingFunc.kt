package com.hannesdorfmann.data.backend.paging

import com.hannesdorfmann.data.pager.Pager
import io.plaidapp.data.PlaidItem
import io.plaidapp.data.api.dribbble.DribbbleService
import io.plaidapp.data.api.dribbble.model.Shot
import rx.Observable
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * The paging function for Dribbble
 *
 * @author Hannes Dorfmann
 */
class DribbblePagingFunc(private val itemsPerPage: Int = 100, private val backendMethodToCall: (Int, Int) -> Observable<List<Shot>>) : Pager.PagingFunction<List<Shot>> {

    private val currentPage = AtomicInteger()

    override fun call(items: List<Shot>?): Observable<List<Shot>> {


        if (items == null || items.isEmpty()) {
            return Observable.never()
        }

        val nextPage = currentPage.getAndAdd(itemsPerPage)
        return backendMethodToCall(nextPage, itemsPerPage)
    }
}