package com.hannesdorfmann.home.sources

import com.hannesdorfmann.mosby.mvp.MvpPresenter

/**
 *
 *
 * @author Hannes Dorfmann
 */
interface SourceFilterPresenter : MvpPresenter<SourceFilterView> {

    fun loadSources();

    /**
     * Changes the source from enabled to disabled or vice versa
     */
    fun changeEnabled(source: SourceFilterPresentationModel);
}