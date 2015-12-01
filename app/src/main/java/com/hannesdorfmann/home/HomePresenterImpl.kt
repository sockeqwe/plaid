package com.hannesdorfmann.home

import android.util.Log
import com.hannesdorfmann.RxPresenter
import com.hannesdorfmann.data.news.ItemsLoader
import com.hannesdorfmann.scheduler.SchedulerTransformer
import io.plaidapp.data.PlaidItem

/**
 *
 *
 * @author Hannes Dorfmann
 */
class HomePresenterImpl(private val itemsLoader: ItemsLoader<List<PlaidItem>>, scheduler: SchedulerTransformer<List<PlaidItem>>) : HomePresenter, RxPresenter<HomeView, List<PlaidItem>>(scheduler) {


    override fun loadItems() {

        view?.showLoading()

        subscribe(
                itemsLoader.firstPage().map { it ?: emptyList() },
                { // onError
                    it.printStackTrace()
                    view?.showError()
                    Log.d("Test", "HomePresenter loadItems() onError")
                },
                { // onNext
                    if (it.isEmpty()) {
                        view.showNoFiltersSelected()
                    } else {
                        view?.setContentItems(it)
                        view?.showContent()
                    }
                    Log.d("Test", "HomePresenter loadItems() onNext")
                }
        )

    }

    override fun loadMore() {
        view?.showLoadingMore(true)

        subscribe(
                itemsLoader.olderPages().map { it ?: emptyList() },
                { // onError
                    it.printStackTrace()
                    view?.showLoadingMoreError(it)
                    Log.d("Test", "HomePresenter loadMore() onError")
                },
                { // onNext
                    view?.addOlderItems(it)
                    view?.showLoadingMore(false)
                    Log.d("Test", "HomePresenter loadMore() onNext")
                }
        )
    }
}