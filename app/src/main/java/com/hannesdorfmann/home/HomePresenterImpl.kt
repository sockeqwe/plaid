package com.hannesdorfmann.home

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
                itemsLoader.firstPage(),
                { // onError
                    view?.showError()
                },
                { // onNext
                    view?.addOlderItems(it)
                    view?.showContent()
                }
        )

    }

    override fun loadMore() {
        view?.showLoadingMore(true)

        subscribe(
                itemsLoader.olderPages(),
                { // onError
                    view?.showLoadingMoreError(it)
                },
                { // onNext
                    view.addOlderItems(it)
                    view.showLoadingMore(false)
                }
        )
    }
}