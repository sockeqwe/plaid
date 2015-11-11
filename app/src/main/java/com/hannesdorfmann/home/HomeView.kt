package com.hannesdorfmann.home

import com.hannesdorfmann.mosby.mvp.MvpView
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView
import io.plaidapp.data.PlaidItem

/**
 * The HomeView basically displays a list of [PlaidItem]s
 *
 * @author Hannes Dorfmann
 */
interface HomeView : MvpLceView<List<PlaidItem>> {

    /**
     * Show the loading indicator that the next page (older items) are loaded right now
     */
    fun showLoadingOlderItems()

    /**
     * Hide the loading indicator that the next page is
     */
    fun hideLoadingOlderPage()

    /**
     * Adds older items to display on screen
     */
    fun addOlderItems(items: List<PlaidItem>)
}