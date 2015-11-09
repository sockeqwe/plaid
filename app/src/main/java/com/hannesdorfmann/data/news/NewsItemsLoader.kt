package com.hannesdorfmann.data.news

import android.support.v4.util.ArrayMap
import com.hannesdorfmann.data.backend.BackendManager
import com.hannesdorfmann.data.pager.Pager
import com.hannesdorfmann.data.source.Source
import com.hannesdorfmann.data.source.SourceDao
import io.plaidapp.data.PlaidItem
import rx.Observable
import rx.Subscription
import rx.subjects.PublishSubject
import java.util.*

/**
 *
 *
 * @author Hannes Dorfmann
 */
class NewsItemsLoader(val sourceDao: SourceDao, val backendManager: BackendManager, val pagerFactory: NewsItemPagerFactory) {

    private val pagerSubscriptions = ArrayMap<Long, Subscription>()
    private val itemsPublisher: PublishSubject<List<PlaidItem>> = PublishSubject.create();
    private val sourceSubscription: Subscription


    init {

        sourceSubscription = sourceDao.getEnabledSources().subscribe()
        itemsPublisher.doOnUnsubscribe { unsubscribeAll() }
    }

    private fun createSourceObservable(): Observable<List<Source>> {
        return sourceDao.getEnabledSources()
    }

    fun loadItems(): Observable<T> {
        throw  UnsupportedOperationException("Not implemented yet")
    }

    fun nextPage() {
        throw  UnsupportedOperationException("Not implemented yet")
    }

    private fun unsubscribeAll() {
        // Unsubscribe from sources
        sourceSubscription.unsubscribe();

        // Unsubscribe from all pagers
        for (subscription in pagerSubscriptions.values) {
            subscription.unsubscribe()
        }
    }

    internal fun handleSourceChanges(sources: List<Source>) {

        val removedKeys = HashSet<Long>(pagerSubscriptions.keys);
        for (source in sources) {
            val foundPager = pagerSubscriptions.get(source.id)
            if (foundPager == null){
                createAndSubscibePager(source)
            }

        }
    }

    internal fun createAndSubscribePager(source : Source){
        pagerFactory.create()
    }

}