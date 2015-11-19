package com.hannesdorfmann.data.loader.router

import rx.Observable
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * This is a factory that allows pagination
 *
 * @author Hannes Dorfmann
 */
class RouteCaller<T>(private val startPage: Int = 0,
                     private val itemsPerPage: Int,
                     private val backendMethodToCall: (Int, Int) -> Observable<T>) {

    /**
     * Offset for loading more
     */
    private val olderPageOffset = AtomicInteger(startPage)

    /**
     * A queue that is used to retry "older"
     * pages if they have failed before continue with even more older
     */
    private val olderFailedButRetryLater: Queue<Int> = LinkedBlockingQueue<Int>()

    /**
     * Get an observable to load older data from backend.
     */
    fun getOlderWithRetry(): Observable<T> {

        val pageOffset = if (
        olderFailedButRetryLater.isEmpty()) {
            olderPageOffset.addAndGet(itemsPerPage)
        } else {
            olderFailedButRetryLater.poll()
        }

        return backendMethodToCall(pageOffset, itemsPerPage)
                .doOnError { olderFailedButRetryLater.add(pageOffset) }
    }

    /**
     * Get an observable to load the newest data from backend.
     * This method should be invoked on pull to refresh
     */
    fun getNewest(): Observable<T> {
        return backendMethodToCall(startPage, itemsPerPage)
    }

    /**
     * Get the first items from backend. This method should be called when
     * loading items for the first time (not load more nor pull to refresh)
     */
    fun getFirst(): Observable<T> {
        return getNewest()
    }
}