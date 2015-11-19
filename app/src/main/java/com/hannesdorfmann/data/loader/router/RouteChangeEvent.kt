package com.hannesdorfmann.data.loader.router

import com.hannesdorfmann.data.loader.router.RouteCaller
import com.hannesdorfmann.data.source.Source

/**
 * A event to inform that the Route has been changed
 *
 * @author Hannes Dorfmann
 */
open class RouteChangeEvent<T>(val backendCaller: RouteCaller<T>) {
    open class RouteAddedEvent<T>(backendCaller: RouteCaller<T>) : RouteChangeEvent<T>(backendCaller)
    open class RouteRemovedEvent<T>(backendCaller: RouteCaller<T>) : RouteChangeEvent<T>(backendCaller)
}
