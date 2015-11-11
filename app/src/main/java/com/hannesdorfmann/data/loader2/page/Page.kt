package com.hannesdorfmann.data.loader2.page

import com.hannesdorfmann.data.loader2.BackendCallFactory
import com.hannesdorfmann.scheduler.SchedulerTransformer
import io.plaidapp.data.PlaidItem
import rx.Observable
import rx.subjects.PublishSubject
import java.util.concurrent.atomic.AtomicInteger

/**
 * A page is representing a special class to concatenate different backend calls by using [BackendCallFactory].
 * Use [#asObservable] to get the observable
 *
 * @author Hannes Dorfmann
 */
abstract class Page<T>(val backendCalls: List<BackendCallFactory<T>>) {

    var failed = AtomicInteger()
        private set

    private val backendCallsCount: Int


    init {
        if (backendCalls == null || backendCalls.isEmpty()) {
            throw IllegalArgumentException("BackendCalls can not be null or empty")
        }
        backendCallsCount = backendCalls.size
    }


    /**
     * Return an observable for this page
     */
    fun asObservable(): Observable<T> {
        val observables = arrayListOf<Observable<T>>()

        for (backendCall in backendCalls) {
            val observable = getBackendCall(backendCall).onErrorResumeNext { // Suppress errors as long as not all fail
                val fails = failed.incrementAndGet()
                if (fails == backendCallsCount) {
                    Observable.error(it) // All failed so emmit error
                } else {
                    Observable.empty() // Not all failed, so ignore this error and emit nothing
                }
            }

            observables.add(observable);
        }

        return Observable.concat(Observable.from(observables))
    }

    protected abstract fun getBackendCall(callFactory: BackendCallFactory<T>): Observable<T>

}