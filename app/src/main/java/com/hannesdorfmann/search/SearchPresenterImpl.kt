package com.hannesdorfmann.search

import com.hannesdorfmann.RxPresenter
import com.hannesdorfmann.data.loader.search.SearchItemsLoaderFactory
import com.hannesdorfmann.data.news.ItemsLoader
import com.hannesdorfmann.scheduler.SchedulerTransformer
import io.plaidapp.data.PlaidItem

/**
 *
 *
 * @author Hannes Dorfmann
 */
class SearchPresenterImpl(private val itemsLoaderFactory: SearchItemsLoaderFactory, scheduler: SchedulerTransformer<List<PlaidItem>>) : SearchPresenter, RxPresenter<SearchView, List<PlaidItem>>(scheduler) {

    private var itemsLoader: ItemsLoader<List<PlaidItem>>? = null

    override fun search(query: String) {

        // Create items loader for the given query string
        itemsLoader = itemsLoaderFactory.create(query)

        view?.showLoading()

        subscribe(itemsLoader!!.firstPage().map { it ?: emptyList() }, { // Error handling
            view?.showError(it)
        }, { // onNext
            view?.setContentItems(it)
        }, {
            view?.showContent()
        })
    }

    override fun searchMore(query: String) {

        view?.showLoadingMore(true)
        subscribe(itemsLoader!!.olderPages().map { it ?: emptyList() }, { // Error handling
            view?.showLoadingMore(false)
            view?.showLoadingMoreError(it)
        }, { // onNext
            view?.addOlderItems(it)
        }, { // onComplete
            view?.showLoadingMore(false)
        })

    }

    override fun clearSearch() {
        // Unsubscribe any previous search subscriptions
        unsubscribe()

        view.showSearchNotStarted()
    }
}