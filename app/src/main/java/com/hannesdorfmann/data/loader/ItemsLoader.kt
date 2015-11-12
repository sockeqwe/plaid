package com.hannesdorfmann.data.news

import android.support.v4.util.ArrayMap
import com.hannesdorfmann.data.backend.BackendManager
import com.hannesdorfmann.data.loader.BackendCaller
import com.hannesdorfmann.data.loader.BackendRouter
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
 * Class responsible to load items from backend by using a [BackendRouter]
 * @author Hannes Dorfmann
 */
class ItemsLoader<I, O>(protected val backendRouter: BackendRouter<I, O>, protected val olderPagesInternalScheduler: SchedulerTransformer<O> = IoSchedulerTransformer()) {


    /**
     * Class representing a result from loading an older page,
     * since we don't want to interrupt the observable subscription onError,
     * we wrap the result into this data structure on publish it in onNext()
     */
    sealed abstract class OlderPageResult<T>() {
        class SuccessfulOlderPageResult<T>(val result: T) : OlderPageResult<T>()
        class ErrorOlderPageResult<T>(val error: Throwable) : OlderPageResult<T> ()
    }

    private val olderPages = PublishSubject.create<OlderPageResult<O>>()
    private val olderPagesSubscriptions = CompositeSubscription()

    init {
        olderPages.doOnUnsubscribe { olderPagesSubscriptions.unsubscribe() }
    }

    fun firstPage(): Observable<O> {
        return FirstPage<O>(backendRouter.getAllBackendCallers()).asObservable()
    }

    fun olderPages(): Observable<OlderPageResult<O>> {
        return olderPages
    }

    fun loadOlderPage() {
        val subscription = OlderPage<O>(backendRouter.getAllBackendCallers())
                .asObservable()
                .compose(olderPagesInternalScheduler)
                .map { OlderPageResult.SuccessfulOlderPageResult<O>(it) }
                .subscribe(
                        { olderPages.onNext(it) },
                        { olderPages.onNext(OlderPageResult.ErrorOlderPageResult(it)) }, // Error are forwarded to onNext
                        { // TODO How to detect if no more older pages available ?
                        })

        olderPagesSubscriptions.add(subscription)
    }

    fun newestPage(): Observable<O> {
        return NewestPage<O>(backendRouter.getAllBackendCallers()).asObservable()
    }

}