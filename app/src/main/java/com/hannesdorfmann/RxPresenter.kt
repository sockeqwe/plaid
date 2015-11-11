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
open class RxPresenter <V : MvpView, M>(private val scheduler: SchedulerTransformer<M>) : MvpBasePresenter<V>() {

    private val subscribers = CompositeSubscription()

    protected fun subscribe(observable: Observable<M>,
                            onError: (Throwable) -> Unit,
                            onNext: (M) -> Unit,
                            onComplete: (() -> Unit)? = null): Subscription {


        if (onComplete == null) {
            val sub = observable.compose(scheduler).subscribe(onNext, onError);
            subscribers.add(sub)
            return sub
        } else {
            val sub = observable.compose(scheduler).subscribe(onNext, onError, onComplete)
            subscribers.add(sub)
            return sub
        }
    }

    override fun detachView(retainInstance: Boolean) {
        super.detachView(retainInstance)
        if (!retainInstance) {
            subscribers.unsubscribe()
        }
    }

}