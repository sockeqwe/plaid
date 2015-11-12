package com.hannesdorfmann.data.loader

import android.support.v4.util.SparseArrayCompat
import com.hannesdorfmann.data.backend.BackendId
import rx.Observable
import java.util.*

/**
 * This "Router" is responsible to pick the right [BackendCallerFactory] for the given backends identified by an unique backend id
 *
 * @see [com.hannesdorfmann.data.backend.BackendManager]
 * @author Hannes Dorfmann
 */
class BackendRouter<I, O> {

    private val routes = SparseArrayCompat<BackendCallerFactory<O>>()

    fun addRoute(@BackendId backendId: Int, backendCallFactory: BackendCallerFactory<O>) {
        if (routes.get(backendId) != null) {
            throw IllegalArgumentException("A route from backend with id = ${backendId} to a ${backendCallFactory} has already been defined!")
        }

        routes.put(backendId, backendCallFactory)
    }

    fun route(@BackendId backendId: Int): BackendCallerFactory<O> {
        return routes.get(backendId)!! // Throws an Exception if no BackendCallFactory registered for given backend id
    }

    /**
     * Get all available routes
     */
    fun getAllRoutes(): List<BackendCallerFactory<O>> {
        val size = routes.size() - 1
        val factories = ArrayList<BackendCallerFactory<O>>()

        for (i in 0..size) {
            val key = routes.keyAt(i)
            factories.add(routes.get(key))
        }

        return factories
    }

    fun getAllBackendCallers(): Observable<List<BackendCaller<O>>> {
        throw UnsupportedOperationException()
    }
}