package com.hannesdorfmann.data.loader.home

import android.support.v4.util.ArrayMap
import android.support.v4.util.SparseArrayCompat
import android.util.Log
import com.hannesdorfmann.data.backend.BackendManager
import com.hannesdorfmann.data.backend.BackendManager.ID
import com.hannesdorfmann.data.loader.router.RouteCaller
import com.hannesdorfmann.data.loader.router.RouteCallerFactory
import com.hannesdorfmann.data.loader.search.DribbbleSearchCallerFactory
import com.hannesdorfmann.data.pager.Pager
import com.hannesdorfmann.data.source.Source
import com.hannesdorfmann.data.source.SourceDao
import io.plaidapp.data.PlaidItem
import io.plaidapp.data.api.dribbble.DribbbleSearch
import io.plaidapp.data.api.dribbble.DribbbleService
import io.plaidapp.data.api.dribbble.model.Shot
import rx.Observable
import java.util.*

/**
 * A [PagerFactory] that creates [Pager] for Dribbble [Source]s
 *
 * @author Hannes Dorfmann
 */
class HomeDribbbleCallerFactory(private val backend: DribbbleService, sourceDao: SourceDao) : RouteCallerFactory<List<PlaidItem>> {

    companion object {
        /**
         * How many items should be loaded per page
         */
        private const val ITEMS_PER_PAGE = 8
    }

    private val backendCalls = ArrayMap<Long, RouteCaller<List<PlaidItem>>>()
    private val sources: Observable<List<Source>>


    init {
        sources = checkedDefer {
            sourceDao.getSourcesForBackend(ID.DRIBBBLE).share()
        }.doOnNext {
            Log.d("Test", "sources doOnNext ${it}")
        }
    }

    private fun createCaller(source: Source): RouteCaller<List<PlaidItem>> {
        return RouteCaller(0, ITEMS_PER_PAGE, getBackendMethodToInvoke(source))
    }

    // TODO make a Factory / Plugin mechanism for this as well
    private fun getBackendMethodToInvoke(source: Source):
            (pageOffset: Int, itemsPerPage: Int) -> Observable<List<PlaidItem>> = when (source.id) {
        Source.ID.DRIBBBLE_POPULAR -> getPopular
        Source.ID.DRIBBBLE_FOLLOWING -> getFollowing
        Source.ID.DRIBBLE_ANIMATED -> getAnimated
        Source.ID.DRIBBLE_DEBUTS -> getDebuts
        Source.ID.DRIBBLE_RECENT -> getRecent
        Source.ID.DRIBBLE_MY_LIKES -> getMyLikes
        Source.ID.DRIBBLE_MY_SHOTS -> getUserShots
        Source.ID.DRIBBLE_MATERIAL -> SearchFunc(source.name!!)

    // Custom Search
        else -> SearchFunc(source.name!!)
    }

    override fun getAllBackendCallers(): Observable<List<RouteCaller<List<PlaidItem>>>> {
        return sources.map(mapSourcesToBackendCalls).doOnNext {
            Log.d("Test", "HomeDribbleBackendCallFactory: getAllBackendCallers() doOnNext ${it}")
        }

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
                    val newCall = createCaller(source)
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

        Log.d("Test", "HomeDribbleBackendCallFactory: Factory created calls: ${calls.size}")
        return calls
    }


    val getPopular = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getPopular(pageOffset, itemsPerPage).doOnNext {
            Log.d("Test", "getPopular() doOnNext ${it}")
        } as Observable<List<PlaidItem>>
    }

    val getFollowing = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getFollowing(pageOffset, itemsPerPage).doOnNext {
            Log.d("Test", "getFollowing() doOnNext ${it}")
        } as Observable<List<PlaidItem>>
    }

    val getAnimated = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getAnimated(pageOffset, itemsPerPage).doOnNext {
            Log.d("Test", "getAnimated() doOnNext ${it}")
        } as Observable<List<PlaidItem>>
    }

    val getDebuts = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getDebuts(pageOffset, itemsPerPage).doOnNext {
            Log.d("Test", "getDebuts() doOnNext ${it}")
        } as Observable<List<PlaidItem>>
    }

    val getRecent = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getRecent(pageOffset, itemsPerPage).doOnNext {
            Log.d("Test", "getRecent() doOnNext ${it}")
        } as Observable<List<PlaidItem>>
    }

    val getMyLikes = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getUserLikes(pageOffset, itemsPerPage) as Observable<List<PlaidItem>>
    }

    val getUserShots = fun(pageOffset: Int, itemsPerPage: Int): Observable<List<PlaidItem>> {
        return backend.getUserShots(pageOffset, itemsPerPage) as Observable<List<PlaidItem>>
    }


    private class SearchFunc(val queryString: String) : (Int, Int) -> Observable<List<PlaidItem>> {

        override fun invoke(pageOffset: Int, pageLimit: Int): Observable<List<PlaidItem>> {

            return Observable.defer<List<PlaidItem>> {
                try {
                    Observable.just(
                        DribbbleSearch.search(queryString, DribbbleSearch.SORT_RECENT, pageOffset) as List<PlaidItem>)
                } catch(e: Exception) {
                    throw RuntimeException(e)
                }
            }.doOnNext {
                Log.d("Test", "Dribbble Search(${queryString}) doOnNext ${it}")
            }
        }
    }

}

