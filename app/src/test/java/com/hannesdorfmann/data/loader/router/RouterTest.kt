package com.hannesdorfmann.data.loader

import com.hannesdorfmann.data.loader.router.RouteCaller
import com.hannesdorfmann.data.loader.router.RouteCallerFactory
import com.hannesdorfmann.data.loader.router.Router
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber
import rx.subjects.PublishSubject
import rx.subjects.TestSubject
import kotlin.test.fail

/**
 *
 *
 * @author Hannes Dorfmann
 */
class RouterTest {


    @Test
    fun notImplemented() {

        // Route
        val route1Observable1 = PublishSubject.create<Int>()
        val route1Observable2 = PublishSubject.create<Int>()
        val route1Call1 = RouteCallerTest.CallMock(route1Observable1)
        val route1Call2 = RouteCallerTest.CallMock(route1Observable2)


        val route2Observable1 = PublishSubject.create<Int>()
        val route2Call1 = RouteCallerTest.CallMock(route2Observable1)

        val route1Caller1 = RouteCaller(0, 10, route1Call1)
        val route1Caller2 = RouteCaller(0, 10, route1Call2)
        val route2Caller1 = RouteCaller(0, 10, route2Call1);


        val factory1 = object : RouteCallerFactory<Int> {
            override fun getAllBackendCallers(): Observable<List<RouteCaller<Int>>> {
                return checkedDefer { Observable.just(listOf(route1Caller1, route1Caller2)) }
            }
        }


        val factory2 = object : RouteCallerFactory<Int> {
            override fun getAllBackendCallers(): Observable<List<RouteCaller<Int>>> {
                return Observable.just(arrayListOf(route2Caller1))
            }
        }


        val router = Router(arrayListOf(factory1, factory2))

        val subscriber = TestSubscriber<List<RouteCaller<Int>>>()
        router.getAllRoutes().subscribe(subscriber)

        subscriber.assertValue(arrayListOf(route1Caller1, route1Caller2, route2Caller1))
        subscriber.assertCompleted()

    }

    // TODO test for dynamic route changes
}