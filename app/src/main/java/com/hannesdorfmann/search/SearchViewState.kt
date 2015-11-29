package com.hannesdorfmann.search

import com.hannesdorfmann.mosby.mvp.viewstate.ViewState
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState
import io.plaidapp.data.PlaidItem

/**
 * ViewState for [SearchView]
 *
 * @author Hannes Dorfmann
 */
class SearchViewState : ViewState<SearchView> {

    private enum class State {
        SHOW_SEARCH_NOT_STARTED, SHOW_LOADING, SHOW_CONTENT, SHOW_ERROR
    }

    private var state = State.SHOW_SEARCH_NOT_STARTED
    private var exception: Throwable? = null
    private var items: List<PlaidItem>? = null
    private var loadingMore = false

    override fun apply(view: SearchView, retained: Boolean) =

            when (state) {
                State.SHOW_SEARCH_NOT_STARTED -> view.showSearchNotStarted()
                State.SHOW_LOADING -> view.showLoading()
                State.SHOW_ERROR -> view.showError(exception!!)
                State.SHOW_CONTENT -> {
                    view.setContentItems(items!!)
                    view.showLoadingMore(loadingMore)
                    view.showContent()
                }
            }

    fun setShowSearchNotStarted() {
        state = State.SHOW_SEARCH_NOT_STARTED
        exception = null
        items = null
        loadingMore = false
    }

    fun setShowLoading() {
        state = State.SHOW_LOADING
        exception = null
        items = null
        loadingMore = false
    }

    fun setShowLoadingMore(loading: Boolean) {
        loadingMore = loading
    }

    fun setShowContent(currentItems: List<PlaidItem>) {
        state = State.SHOW_CONTENT
        exception = null
        items = currentItems
        loadingMore = false
    }

    fun setShowError(t: Throwable) {
        state = State.SHOW_SEARCH_NOT_STARTED
        exception = t
        items = null
        loadingMore = false
    }

}