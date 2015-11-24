package com.hannesdorfmann.data.loader

import com.hannesdorfmann.data.loader.router.RouteCaller
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import rx.Observable
import rx.observers.TestSubscriber
import rx.subjects.PublishSubject
import rx.subjects.TestSubject
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 *
 *
 * @author Hannes Dorfmann
 */
class RouteCallerTest {

    class CallMock(var observable: Observable<Int>) : (Int, Int) -> Observable<Int> {

        var startOffset = -1
        var itemsPerPage = -1

        override fun invoke(startOffset: Int, itemsPerPage: Int): Observable<Int> {
            this.startOffset = startOffset
            this.itemsPerPage = itemsPerPage
            return observable
        }
    }

    val startOffset = 0
    val itemsPerPage = 10;
    lateinit var routeCaller: RouteCaller<Int>
    lateinit var observable: PublishSubject<Int>

    lateinit var call: CallMock

    @Before
    fun init() {

        observable = PublishSubject.create()
        call = CallMock(observable)

        routeCaller = RouteCaller(startOffset, itemsPerPage, call)
    }

    @Test
    fun getFirst() {
        routeCaller.getFirst()
        assertCallInvoked(startOffset, itemsPerPage)
    }

    @Test
    fun getNewest() {
        routeCaller.getNewest()
        assertCallInvoked(startOffset, itemsPerPage)
    }

    @Test fun getOlderWithRetry() {
        var subscriber = TestSubscriber<Int>()
        routeCaller.getOlderWithRetry().subscribe(subscriber)

        val pageOffset = startOffset + itemsPerPage;
        assertCallInvoked(pageOffset, itemsPerPage)


        // Should retry with previous failed offset
        val error = Exception("mock")
        observable.onError(error)
        routeCaller.getOlderWithRetry()
        assertCallInvoked(pageOffset, itemsPerPage)
        subscriber.assertError(error)


        // Run with the next
        routeCaller.getOlderWithRetry()
        assertCallInvoked(pageOffset + itemsPerPage, itemsPerPage)

    }

    private fun assertCallInvoked(startOffset: Int, itemsPerPage: Int) {
        assertEquals(startOffset, call.startOffset)
        assertEquals(itemsPerPage, call.itemsPerPage)
    }
}