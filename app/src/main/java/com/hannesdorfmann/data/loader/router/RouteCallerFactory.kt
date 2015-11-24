package com.hannesdorfmann.data.loader.router

import com.hannesdorfmann.data.loader.router.RouteChangeEvent
import com.hannesdorfmann.data.source.Source
import rx.Observable

/**
 * Responsible to create a [RouteCaller]
 *
 * @param O The output type of what kind of observable the [RouteCaller] is going to produce
 * @author Hannes Dorfmann
 */
interface RouteCallerFactory<O> {

    /**
     * Get all available backend callers
     */
    fun getAllBackendCallers(): Observable<List<RouteCaller<O>>>

    /**
     * Little helper function to create an observable deferred with checked exceptions
     */
    fun checkedDefer<T>(observable: () -> Observable<T>): Observable<T> {

        return Observable.defer<T> {
            try {
                observable()
            } catch(e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

}
