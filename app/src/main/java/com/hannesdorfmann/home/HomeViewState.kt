package com.hannesdorfmann.home

import com.hannesdorfmann.mosby.mvp.viewstate.ViewState
import io.plaidapp.data.PlaidItem

/**
 * @author Hannes Dorfmann
 */
class HomeViewState : ViewState<HomeView> {

    enum class State {
        SHOW_LOADING, SHOW_ERROR, SHOW_CONTENT, SHOW_NO_FILTERS
    }

    var state: State = State.SHOW_LOADING
    private var items: List<PlaidItem>? = null
    private var loadingMore = false

    override fun apply(view: HomeView, retained: Boolean) = when (state) {

        State.SHOW_LOADING -> view.showLoading()
        State.SHOW_ERROR -> view.showError()
        State.SHOW_NO_FILTERS -> view.showNoFiltersSelected()
        State.SHOW_CONTENT -> {
            view.setContentItems(items!!)
            view.showLoadingMore(loadingMore)
        }

    }

    fun setShowLoading() {
        state = State.SHOW_LOADING
        items = null
    }

    fun setShowError() {
        state = State.SHOW_ERROR
        items = null
    }

    fun setShowNoFilters() {
        state = State.SHOW_NO_FILTERS
        items = null
    }

    fun setShowContent(items: List<PlaidItem>?) {
        state = State.SHOW_CONTENT
        this.items = items
    }

    fun setShowLoadingMore(loadMore: Boolean) {
        state = State.SHOW_CONTENT
        this.loadingMore = loadMore
    }
}
