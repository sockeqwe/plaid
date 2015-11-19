package com.hannesdorfmann.data.backend.paging

import android.support.v4.util.ArrayMap
import android.support.v4.util.SparseArrayCompat
import com.hannesdorfmann.data.backend.BackendManager
import com.hannesdorfmann.data.loader.router.RouteCaller
import com.hannesdorfmann.data.loader.router.RouteCallerFactory
import com.hannesdorfmann.data.pager.Pager
import com.hannesdorfmann.data.source.Source
import com.hannesdorfmann.data.source.SourceDao
import io.plaidapp.data.PlaidItem
import io.plaidapp.data.api.dribbble.DribbbleService
import io.plaidapp.data.api.dribbble.model.Shot
import rx.Observable
import java.util.*

/**
 * A [PagerFactory] that creates [Pager] for Dribbble [Source]s
 *
 * @author Hannes Dorfmann
 */
class HomeDribbbleBackendCallFactory(private val backend: DribbbleService, sourceDao: SourceDao) : RouteCallerFactory<List<PlaidItem>> {

    companion object {
        /**
         * How many items should be loaded per page
         */
        private const val ITEMS_PER_PAGE = 100
    }

    private val backendCalls = ArrayMap<Long, RouteCaller<List<PlaidItem>>>()
    private val sources: Observable<List<Source>>


    init {
        sources = checkedDefer {
            sourceDao.getSourcesForBackend(BackendManager.ID.DRIBBBLE).share()
        }
    }

    private fun createCaller(sourceId: Long): RouteCaller<List<PlaidItem>> {
        return RouteCaller(0, ITEMS_PER_PAGE, getBackendMethodToInvoke(sourceId))
    }

    // TODO make a Factory / Plugin mechanism for this as well
    private fun getBackendMethodToInvoke(sourceId: Long):
            (pageOffset: Int, itemsPerPage: Int) -> Observable<List<PlaidItem>> = when (sourceId) {
        Source.ID.DRIBBBLE_POPULAR -> getPopular
        Source.ID.DRIBBBLE_FOLLOWING -> getFollowing
        Source.ID.DRIBBLE_ANIMATED -> getAnimated
        Source.ID.DRIBBLE_DEBUTS -> getDebuts
        Source.ID.DRIBBLE_RECENT -> getRecent
        Source.ID.DRIBBLE_MY_LIKES -> getMyLikes
        Source.ID.DRIBBLE_MY_SHOTS -> getUserShots

    // TODO custom "search"
        else -> throw IllegalArgumentException("Don't know how to create a ${RouteCaller::class.simpleName} from this ${Source::class.simpleName} with id ${sourceId}")
    }

    override fun getAllBackendCallers(): Observable<List<RouteCaller<List<PlaidItem>>>> {
        return sources.map(mapSourcesToBackendCalls)

    }

    /**
     * Transforms / maps a `List
     */
    val mapSourcesToBackendCalls = fun(sources: List<Source>): List<RouteCaller<List<PlaidItem>>> {
        val calls = ArrayList<RouteCaller<List<PlaidItem>>>()
        sources.forEach { source ->

            val call = backendCalls[source.id]

            if (call == null) {
                // New source added
                if (source.enabled) {
                    val newCall = createCaller(source.id)
                    backendCalls.put(source.id, newCall)
                    calls.add(newCall)
                }

            } else {
                // Already existing source

                if (!source.enabled) {
                    // Source has been disabled
                    backendCalls.remove(source.id)
                } else {
                    calls.add(call)
                }
            }
        }

        return calls
    }


    val getPopular = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getPopular(pageOffset, itemsPerPage) as Observable<List<PlaidItem>>
    }

    val getFollowing = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getFollowing(pageOffset, itemsPerPage) as Observable<List<PlaidItem>>
    }

    val getAnimated = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getAnimated(pageOffset, itemsPerPage) as Observable<List<PlaidItem>>
    }

    val getDebuts = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getDebuts(pageOffset, itemsPerPage) as Observable<List<PlaidItem>>
    }

    val getRecent = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getRecent(pageOffset, itemsPerPage) as Observable<List<PlaidItem>>
    }

    val getMyLikes = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getUserLikes(pageOffset, itemsPerPage) as Observable<List<PlaidItem>>
    }

    val getUserShots = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getUserShots(pageOffset, itemsPerPage) as Observable<List<PlaidItem>>
    }

}

