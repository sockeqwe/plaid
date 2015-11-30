package com.hannesdorfmann.data.loader.router

import android.support.v4.util.SparseArrayCompat
import android.util.Log
import com.hannesdorfmann.data.backend.BackendId
import io.plaidapp.data.PlaidItem
import rx.Observable
import rx.functions.FuncN
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

        Log.d("Test", "Router: found ${callers.size} calls to make")

        /*
        return Observable.merge(callers).collect(
                { ArrayList<RouteCaller<T>>() },
                { completeList, item ->
                    completeList.addAll(item)
                }).map { it as List<RouteCaller<T>> } // convert to immutable list, caused by type safety
                .doOnNext {
                    Log.d("Test", "Router doOnNext() ${it}")
                }

        */

        return Observable.combineLatest(callers, { calls ->
            val items = ArrayList<RouteCaller<T>>(calls.size)
            calls.forEach {
                items.addAll(it as List<RouteCaller<T>>)
            }

            items as List<RouteCaller<T>>
        }).doOnNext {
            Log.d("Test", "Router doOnNext() ${it}")
        }

    }
}
