package com.hannesdorfmann

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby.mvp.MvpView
import com.hannesdorfmann.scheduler.SchedulerTransformer
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
 *
 *
 * @author Hannes Dorfmann
 */
open class RxPresenter <V : MvpView, M>(protected val scheduler: SchedulerTransformer<M>) : MvpBasePresenter<V>() {

    private var subscribers = CompositeSubscription()

    protected fun subscribe(observable: Observable<M?>,
                            onError: (Throwable) -> Unit,
                            onNext: (M) -> Unit,
                            onComplete: (() -> Unit)? = null, unsubscribeAutomatically: Boolean = true): Subscription {


        val sub = if (onComplete == null) {
            observable.compose(scheduler).subscribe(onNext, onError);
        } else {
            observable.compose(scheduler).subscribe(onNext, onError, onComplete)
        }

        if (unsubscribeAutomatically) {
            subscribers.add(sub)
        }

        return sub
    }

    override fun detachView(retainInstance: Boolean) {
        super.detachView(retainInstance)
        if (!retainInstance) {
            unsubscribe()
        }
    }

    protected fun unsubscribe() {
        subscribers.unsubscribe()
        subscribers = CompositeSubscription()
    }

}