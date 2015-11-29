package com.hannesdorfmann.home

import com.hannesdorfmann.mosby.mvp.MvpPresenter

/**
 *
 *
 * @author Hannes Dorfmann
 */
interface HomePresenter : MvpPresenter<HomeView> {

    fun loadItems();

    fun loadMore();
}