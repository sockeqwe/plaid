package com.hannesdorfmann.home.sources

/**
 *
 *
 * @author Hannes Dorfmann
 */
interface SourceFilterPresenter {

    fun loadSources();

    /**
     * Changes the source from enabled to disabled or vice versa
     */
    fun changeEnabled(source: SourceFilterPresentationModel);
}