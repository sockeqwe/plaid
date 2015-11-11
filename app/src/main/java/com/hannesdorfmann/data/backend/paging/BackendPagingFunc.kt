package com.hannesdorfmann.data.backend.paging

import com.hannesdorfmann.data.pager.Pager
import io.plaidapp.data.PlaidItem
import io.plaidapp.data.api.dribbble.DribbbleService
import io.plaidapp.data.api.dribbble.model.Shot
import rx.Observable
import java.util.concurrent.atomic.AtomicInteger


/**
 * Function that checks whether the list of items is empty or not.
 * @return true if more pages are available, otherwise false (NO more pages are available)
 */
fun morePagesAvailableBecauseListNotEmpty(items: List<Any>?): Boolean {
    return items != null && !items.isEmpty()
}

/**
 *
 * The paging function for [Pager] that takes a function reference (Lambda) as parameter
 *
 * @author Hannes Dorfmann
 */
class BackendPagingFunc<T>(private val itemsPerPage: Int = 100, private val backendMethodToCall: (Int, Int) -> Observable<T>, private val morePagesAvailable: (T?) -> Boolean) : Pager.PagingFunction<T> {

    private val currentPage = AtomicInteger()

    override fun call(items: T?): Observable<T> {


        if (!morePagesAvailable(items)) {
            return Observable.never()
        }

        val nextPage = currentPage.getAndAdd(itemsPerPage)
        return backendMethodToCall(nextPage, itemsPerPage)
    }

}