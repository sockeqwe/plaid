package com.hannesdorfmann.home

import com.hannesdorfmann.mosby.mvp.MvpView
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView
import io.plaidapp.data.PlaidItem

/**
 * The HomeView basically displays a list of [PlaidItem]s
 *
 * @author Hannes Dorfmann
 */
interface HomeView : MvpView {

    fun showLoading()

    fun showContent()

    fun showError()

    fun setContentItems(items: List<PlaidItem>)

    fun showLoadingMore(showing: Boolean)

    fun showLoadingMoreError(t: Throwable)

    fun addOlderItems(items: List<PlaidItem>)

    fun showNoFiltersSelected();
}