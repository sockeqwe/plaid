package com.hannesdorfmann.data.backend.paging

import io.plaidapp.data.api.dribbble.DribbbleService
import io.plaidapp.data.api.dribbble.model.Shot
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import rx.Observable
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 *
 *
 * @author Hannes Dorfmann
 */
class DibbblePagingFuncTest {


    private class MockBackendCall() {

        var pageOffset = -1
        var resultsPerPage = -1
        var invoked = 0;

        val call = fun(pageOffset: Int, resultsPerPage: Int): Observable<List<Shot>> {
            this.pageOffset = pageOffset
            this.resultsPerPage = resultsPerPage
            invoked++
            return Observable.empty() // We don't do anything with the result anyway
        }

        fun assertCalledWith(pageOffset: Int, resultsPerPage: Int) {
            assertEquals(pageOffset, this.pageOffset)
            assertEquals(resultsPerPage, this.resultsPerPage)
        }

        fun assertInvoked(times: Int) {
            Assert.assertEquals("MockBackend's call method hasn't been invoked the desired times", times, invoked)
        }

        fun assertNeverInvoked() {
            assertInvoked(0)
        }

        fun assertInvokedOnce() {
            assertInvoked(1)
        }
    }

    val itemsPerPage = 100
    lateinit var backendCall: MockBackendCall
    lateinit var paging: DribbblePagingFunc
    val endOfStream: Observable<List<Shot>> = Observable.never()

    @Before
    fun init() {
        backendCall = MockBackendCall()
        paging = DribbblePagingFunc(itemsPerPage, backendCall.call)
    }

    @Test
    fun invokeNextPage() {

        // Sample data, doesn't really matters
        val previousPage = arrayListOf(Shot.Builder().setId(123).build())

        // Simulate 10 page calls
        val timesRun = 10
        for (i in 0..timesRun - 1) {
            paging.call(previousPage)
            val result = backendCall.assertCalledWith(itemsPerPage * i, itemsPerPage)

            assertNotEquals(endOfStream, result)
        }

        backendCall.assertInvoked(timesRun)

    }

    @Test fun quitOnEmptyResult() {
        val result = paging.call(arrayListOf())
        backendCall.assertNeverInvoked()
        assertEquals(endOfStream, result)
    }

    @Test fun quitOnNullResult() {
        val result = paging.call(null)
        backendCall.assertNeverInvoked()
        assertEquals(endOfStream, result)
    }
}
