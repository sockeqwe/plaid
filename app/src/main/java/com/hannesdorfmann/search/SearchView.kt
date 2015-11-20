package com.hannesdorfmann.search

import com.hannesdorfmann.mosby.mvp.MvpView
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView
import io.plaidapp.data.PlaidItem

/**
 *
 *
 * @author Hannes Dorfmann
 */
interface SearchView : MvpView {
    fun showLoading()
    fun showContent()
    fun showError(t: Throwable)
    fun setContentItems(items: List<PlaidItem>)

    fun showLoadingMore(show: Boolean)
    fun showLoadingMoreError(t: Throwable)
    fun addOlderItems(items: List<PlaidItem>)

    fun showSearchNotStarted()
}