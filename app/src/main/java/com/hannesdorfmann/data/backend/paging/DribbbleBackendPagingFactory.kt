package com.hannesdorfmann.data.backend.paging

import com.hannesdorfmann.data.news.PagerFactory
import com.hannesdorfmann.data.pager.Pager
import com.hannesdorfmann.data.source.Source
import io.plaidapp.data.PlaidItem
import io.plaidapp.data.api.dribbble.DribbbleService
import io.plaidapp.data.api.dribbble.model.Shot
import rx.Observable

/**
 *
 * A [PagerFactory] that creates [Pager] for Dribbble [Source]s
 *
 * @author Hannes Dorfmann
 */
class DribbbleBackendPagingFactory(private val backend: DribbbleService) : PagerFactory<List<PlaidItem>> {

    companion object {
        /**
         * How many items should be loaded per page
         */
        const val ITEMS_PER_PAGE = 100
    }

    // TODO make a factory
    override fun createPager(source: Source): Pager<List<PlaidItem>, List<PlaidItem>> = when (source.id) {
        Source.ID.DRIBBBLE_POPULAR -> Pager.create(getPopular(0, ITEMS_PER_PAGE), BackendPagingFunc(ITEMS_PER_PAGE, getPopular, ::morePagesAvailableBecauseListNotEmpty))
        Source.ID.DRIBBBLE_FOLLOWING -> Pager.create(getFollowing(0, ITEMS_PER_PAGE), BackendPagingFunc(ITEMS_PER_PAGE, getFollowing, ::morePagesAvailableBecauseListNotEmpty))
        Source.ID.DRIBBLE_ANIMATED -> Pager.create(getAnimated(0, ITEMS_PER_PAGE), BackendPagingFunc(ITEMS_PER_PAGE, getAnimated, ::morePagesAvailableBecauseListNotEmpty))
        Source.ID.DRIBBLE_DEBUTS -> Pager.create(getDebuts(0, ITEMS_PER_PAGE), BackendPagingFunc(ITEMS_PER_PAGE, getDebuts, ::morePagesAvailableBecauseListNotEmpty))
        Source.ID.DRIBBLE_RECENT -> Pager.create(getRecent(0, ITEMS_PER_PAGE), BackendPagingFunc(ITEMS_PER_PAGE, getRecent, ::morePagesAvailableBecauseListNotEmpty))
        Source.ID.DRIBBLE_MY_LIKES -> Pager.create(getMyLikes(0, ITEMS_PER_PAGE), BackendPagingFunc(ITEMS_PER_PAGE, getMyLikes, ::morePagesAvailableBecauseListNotEmpty))
        Source.ID.DRIBBLE_MY_SHOTS -> Pager.create(getUserShots(0, ITEMS_PER_PAGE), BackendPagingFunc(ITEMS_PER_PAGE, getUserShots, ::morePagesAvailableBecauseListNotEmpty))

        else -> throw IllegalArgumentException("Don't know how to create a Pager from this Source with id ${source.id}")
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