package com.hannesdorfmann.home

import com.hannesdorfmann.RxPresenter
import com.hannesdorfmann.data.news.NewsItemsLoader
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter
import com.hannesdorfmann.scheduler.SchedulerTransformer
import io.plaidapp.data.PlaidItem
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 *
 *
 * @author Hannes Dorfmann
 */
class HomePresenter(private val itemsLoader: NewsItemsLoader, scheduler: SchedulerTransformer<List<PlaidItem>>) : RxPresenter<HomeView, List<PlaidItem>>(scheduler) {



}