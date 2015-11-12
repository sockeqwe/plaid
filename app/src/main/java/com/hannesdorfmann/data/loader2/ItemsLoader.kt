package com.hannesdorfmann.data.news

import android.support.v4.util.ArrayMap
import com.hannesdorfmann.data.backend.BackendManager
import com.hannesdorfmann.data.loader2.BackendRouter
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
 * Class responsible to load items from backend by using a [BackendRouter]
 * @author Hannes Dorfmann
 */
class ItemsLoader<I, O>(val backendRouter: BackendRouter<I, O>) {


    fun firstPage(): Observable<O> {
        throw UnsupportedOperationException()
    }

    fun olderPages(): Observable<O> {
        throw UnsupportedOperationException()
    }

    fun loadOlderPage() {
        throw UnsupportedOperationException()
    }

    fun newestPage(): Observable<O> {
        throw UnsupportedOperationException()
    }
}