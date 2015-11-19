package com.hannesdorfmann.data.loader.page

import com.hannesdorfmann.data.loader.router.RouteCaller
import io.plaidapp.data.PlaidItem
import rx.Observable

/**
 *
 * A [Page] that loads older pages by using [RouteCaller]
 *
 * @author Hannes Dorfmann
 */
class NewestPage<T>(routeCalls: Observable<List<RouteCaller<T>>>) : Page<T>(routeCalls) {

    override fun getRouteCall(caller: RouteCaller<T>): Observable<T> {
        return caller.getNewest();
    }
}