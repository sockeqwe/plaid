package com.hannesdorfmann.data.loader.router

import com.hannesdorfmann.data.loader.router.RouteCaller
import com.hannesdorfmann.data.source.Source


/**
 * Event that will be triggered to notify that a Source has been added
 */
class SourceAddedEvent<T>(backendCaller: RouteCaller<T>, val source: Source) : RouteChangeEvent.RouteAddedEvent<T>(backendCaller)

/**
 * Event that will be triggered to notify that a Source has been removed
 */
class SourceRemovedEvent<T>(backendCaller: RouteCaller<T>, val source: Source) : RouteChangeEvent.RouteRemovedEvent<T>(backendCaller)