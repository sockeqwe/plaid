package com.hannesdorfmann.data.news

import android.support.v4.util.ArrayMap
import com.hannesdorfmann.data.backend.BackendManager
import com.hannesdorfmann.data.loader.router.RouteCaller
import com.hannesdorfmann.data.loader.router.Router
import com.hannesdorfmann.data.loader.page.FirstPage
import com.hannesdorfmann.data.loader.page.NewestPage
import com.hannesdorfmann.data.loader.page.OlderPage
import com.hannesdorfmann.data.pager.Pager
import com.hannesdorfmann.data.source.Source
import com.hannesdorfmann.data.source.SourceDao
import com.hannesdorfmann.scheduler.IoSchedulerTransformer
import com.hannesdorfmann.scheduler.SchedulerTransformer
import io.plaidapp.data.PlaidItem
import rx.Observable
import rx.Subscription
import rx.subjects.PublishSubject
import rx.subscriptions.CompositeSubscription
import java.util.*

/**
 *
 * Class responsible to load items from backend by using a [Router]
 * @param T The type of the items that are loaded ... Typically list of [PlaidItem]
 * @author Hannes Dorfmann
 */
class ItemsLoader<T>(protected val router: Router<T>) {


    fun firstPage(): Observable<T> {
        return FirstPage<T>(router.getAllRoutes()).asObservable()
    }

    fun olderPages(): Observable<T> {
        return OlderPage<T>(router.getAllRoutes()).asObservable()
    }

    fun newestPage(): Observable<T> {
        return NewestPage<T>(router.getAllRoutes()).asObservable()
    }

}