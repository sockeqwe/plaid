package com.hannesdorfmann.data.loader

import rx.Observable

/**
 * Responsible to create a [BackendCaller]
 *
 * @param I The input type. i.e. a [Source]
 * @param O The output type of what kind of observable the [BackendCaller] is going to produce
 * @author Hannes Dorfmann
 */
interface BackendCallerFactory<O> {

    /**
     * Get all available backend callers
     */
    fun getAllBackendCallers(): Observable<List<BackendCaller<O>>>
}
