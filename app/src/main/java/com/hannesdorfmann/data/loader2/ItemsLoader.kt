package com.hannesdorfmann.data.news

import android.support.v4.util.ArrayMap
import com.hannesdorfmann.data.backend.BackendManager
import com.hannesdorfmann.data.pager.Pager
import com.hannesdorfmann.data.source.Source
import com.hannesdorfmann.data.source.SourceDao
import com.hannesdorfmann.scheduler.IoSchedulerTransformer
import com.hannesdorfmann.scheduler.SchedulerTransformer
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
class ItemsLoader(val sourceDao: SourceDao,
                  val pagerFactory: NewsItemPagerFactory,
                  private val databaseScheduler: SchedulerTransformer<List<Source>> = IoSchedulerTransformer(),
                  private val sourcePagerScheduler: SchedulerTransformer<List<PlaidItem>> = IoSchedulerTransformer()) {

    private val pagerSubscriptions = ArrayMap<Long, Subscription>()
    private lateinit var sourceSubscription: Subscription
    val items: PublishSubject<List<PlaidItem>> = PublishSubject.create();
    val newerItems: Observable<List<PlaidItem>> = PublishSubject.create();
    val olderItems: Observable<List<PlaidItem>> = PublishSubject.create();


    init {
        items.doOnSubscribe {
            sourceSubscription = sourceDao.getEnabledSources().compose(databaseScheduler).subscribe({ handleSourceChanges(it) }, { items.onError(it) })
        }
    }

    private fun createSourceObservable(): Observable<List<Source>> {
        return sourceDao.getEnabledSources()
    }

    fun loadItems(): Observable<List<PlaidItem>> {
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
            if (foundPager == null) {
                // No pager found, so it's a new source
                val pager = pagerFactory.create(source)
                // TODO implement onError and onCompleted
                val subscription = pager.start().compose(sourcePagerScheduler).subscribe({ items.onNext(it) }, {}, {})
                pagerSubscriptions.put(source.id, subscription)
            } else {
                // pager already exists, so nothing has changed
                removedKeys.remove(source.id)
            }
        }

        for (sourceId in removedKeys) {
            // Some Sources has been disabled or removed, so cancel any ongoing pager subscription
            val subscription = pagerSubscriptions.get(sourceId)
            if (subscription != null) {
                subscription.unsubscribe()
                pagerSubscriptions.remove(sourceId)
            }
        }
    }

}