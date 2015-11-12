package com.hannesdorfmann.data.loader

/**
 * Responsible to create a [BackendCaller]
 *
 * @param I The input type. i.e. a [Source]
 * @param O The output type of what kind of observable the [BackendCaller] is going to produce
 * @author Hannes Dorfmann
 */
interface BackendCallerFactory<I, O> {

    /**
     * Create a [BackendCaller] depending on the given input
     * @param inputType The data information  to lookup and create the corresponding [BackendCaller]
     */
    fun getBackendCaller(inputType: I): BackendCaller<O>

    /**
     * Get all available backend callers
     */
    fun getAllBackendCallers(): List<BackendCaller<O>>
}
