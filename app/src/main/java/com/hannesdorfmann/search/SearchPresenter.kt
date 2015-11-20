package com.hannesdorfmann.search

import com.hannesdorfmann.mosby.mvp.MvpPresenter

/**
 *
 *
 * @author Hannes Dorfmann
 */
interface SearchPresenter : MvpPresenter<SearchView> {

    fun search(query: String)

    fun searchMore(query: String)

    fun clearSearch()
}