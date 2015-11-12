package com.hannesdorfmann.data.backend.paging

import com.hannesdorfmann.data.loader2.BackendCaller
import com.hannesdorfmann.data.loader2.BackendCallerFactory
import com.hannesdorfmann.data.news.PagerFactory
import com.hannesdorfmann.data.pager.Pager
import com.hannesdorfmann.data.source.Source
import io.plaidapp.data.PlaidItem
import io.plaidapp.data.api.dribbble.DribbbleService
import io.plaidapp.data.api.dribbble.model.Shot
import rx.Observable

/**
 * A [PagerFactory] that creates [Pager] for Dribbble [Source]s
 *
 * @author Hannes Dorfmann
 */
class HomeDribbbleBackendCallFactory(private val backend: DribbbleService) : BackendCallerFactory<Source, List<PlaidItem>> {

    companion object {
        /**
         * How many items should be loaded per page
         */
        const val ITEMS_PER_PAGE = 100
    }


    override fun createBackendCaller(inputType: Source): BackendCaller<List<PlaidItem>> {
        return BackendCaller(0, ITEMS_PER_PAGE, getBackendMethodToInvoke(inputType))
    }

    // TODO make a Factory / Plugin mechanism for this as well
    internal fun getBackendMethodToInvoke(source: Source):
            (pageOffset: Int, itemsPerPage: Int) -> Observable<List<PlaidItem>> = when (source.id) {
        Source.ID.DRIBBBLE_POPULAR -> getPopular
        Source.ID.DRIBBBLE_FOLLOWING -> getFollowing
        Source.ID.DRIBBLE_ANIMATED -> getAnimated
        Source.ID.DRIBBLE_DEBUTS -> getDebuts
        Source.ID.DRIBBLE_RECENT -> getRecent
        Source.ID.DRIBBLE_MY_LIKES -> getMyLikes
        Source.ID.DRIBBLE_MY_SHOTS -> getUserShots

        else -> throw IllegalArgumentException("Don't know how to create a ${BackendCaller::class.simpleName} from this ${Source::class.simpleName} with id ${source.id}")
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