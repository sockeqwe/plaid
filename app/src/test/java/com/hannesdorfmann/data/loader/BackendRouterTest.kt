package com.hannesdorfmann.data.loader

import com.hannesdorfmann.data.loader2.BackendCallerFactory
import com.hannesdorfmann.data.loader2.BackendRouter
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 *
 *
 * @author Hannes Dorfmann
 */
class BackendRouterTest {

    lateinit var router: BackendRouter<Any, Any>

    @Before
    fun init() {
        router = BackendRouter()
    }

    @Test(expected = IllegalArgumentException::class)
    fun routeAlreadyExists() {

        val factory1 = Mockito.mock(BackendCallerFactory::class.java) as BackendCallerFactory<Any, Any>
        val factory2 = Mockito.mock(BackendCallerFactory::class.java) as BackendCallerFactory<Any, Any>

        router.addRoute(1, factory1)
        router.addRoute(1, factory2) // Should throw an exception
    }

    @Test
    fun getRoute() {
        val fac1Id = 23
        val fac2Id = 7

        val factory1 = Mockito.mock(BackendCallerFactory::class.java) as BackendCallerFactory<Any, Any>
        val factory2 = Mockito.mock(BackendCallerFactory::class.java) as BackendCallerFactory<Any, Any>

        router.addRoute(fac1Id, factory1)
        router.addRoute(fac2Id, factory2)

        assertTrue(router.route(fac1Id) === factory1)
        assertTrue(router.route(fac2Id) === factory2)
    }

    @Test
    fun getAllRoutes(){
        val factory1 = Mockito.mock(BackendCallerFactory::class.java) as BackendCallerFactory<Any, Any>
        val factory2 = Mockito.mock(BackendCallerFactory::class.java) as BackendCallerFactory<Any, Any>
        val factory3 = Mockito.mock(BackendCallerFactory::class.java) as BackendCallerFactory<Any, Any>

        router.addRoute(23, factory1)
        router.addRoute(7, factory2)
        router.addRoute(80, factory3)


        val allRoutes = router.getAllRoutes()
        assertEquals(3, allRoutes.size)

        assertTrue(allRoutes.contains(factory1));
        assertTrue(allRoutes.contains(factory2));
        assertTrue(allRoutes.contains(factory3));

    }

}