package com.hannesdorfmann.data.loader.page

import com.hannesdorfmann.data.loader.router.RouteCaller
import com.hannesdorfmann.scheduler.SchedulerTransformer
import io.plaidapp.data.PlaidItem
import rx.Observable
import rx.subjects.PublishSubject
import java.util.concurrent.atomic.AtomicInteger

/**
 * A page is representing a special class to concatenate different backend calls by using [RouteCaller].
 * Use [#asObservable] to get the observable
 *
 * @author Hannes Dorfmann
 */
abstract class Page<T>(val routeCalls: Observable<List<RouteCaller<T>>>) {

    var failed = AtomicInteger()
        private set

    private var backendCallsCount: Int = 0

    /**
     * Return an observable for this page
     */
    fun asObservable(): Observable<T> {

        return routeCalls.flatMap { routeCalls ->

            val observables = arrayListOf<Observable<T>>()

            routeCalls.forEach { call ->
                val observable = getRouteCall(call).onErrorResumeNext { error -> // Suppress errors as long as not all fail
                    val fails = failed.incrementAndGet()
                    if (fails == backendCallsCount) {
                        Observable.error(error) // All failed so emmit error
                    } else {
                        Observable.empty() // Not all failed, so ignore this error and emit nothing
                    }
                }
                observables.add(observable);
            }

            // return the created Observable
            Observable.merge(observables)
        }
    }

    protected abstract fun getRouteCall(caller: RouteCaller<T>): Observable<T>

}