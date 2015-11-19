package com.hannesdorfmann.data.loader.router

import android.support.v4.util.SparseArrayCompat
import com.hannesdorfmann.data.backend.BackendId
import rx.Observable
import java.util.*

/**
 * This "Router" is responsible to pick the right [RouteCallerFactory] for the given backends identified by an unique backend id
 *
 * @see [com.hannesdorfmann.data.backend.BackendManager]
 * @author Hannes Dorfmann
 */
class Router<T>(private val routeFactories: List<RouteCallerFactory<T>>) {


    fun getAllRoutes(): Observable<List<RouteCaller<T>>> {
        val callers = ArrayList<Observable<List<RouteCaller<T>>>>()

        routeFactories.forEach {
            callers.add(it.getAllBackendCallers())
        }

        return Observable.merge(callers).collect(
                { ArrayList<RouteCaller<T>>() },
                { completeList, item ->
                    completeList.addAll(item)
                }).map { it } // convert to immutable list, caused by type safety

    }
}
