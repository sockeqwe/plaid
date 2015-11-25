package com.hannesdorfmann.home.sources

import com.hannesdorfmann.RxPresenter
import com.hannesdorfmann.data.source.Source
import com.hannesdorfmann.data.source.SourceDao
import com.hannesdorfmann.home.sources.SourceFilterPresentationModel
import com.hannesdorfmann.scheduler.SchedulerTransformer
import rx.Observable

/**
 *
 *
 * @author Hannes Dorfmann
 */
class SourceFilterPresenterImpl(val sourceDao: SourceDao, val presentationModelMapper: (List<Source>) -> List<SourceFilterPresentationModel>, scheduler: SchedulerTransformer<List<SourceFilterPresentationModel>>) : SourceFilterPresenter, RxPresenter<SourceFilterView, List<SourceFilterPresentationModel>>(scheduler) {

    override fun loadSources() {

        view?.showLoading(false)

        subscribe(
                sourceDao.getAllSources().map(presentationModelMapper),
                // onError
                {
                    view?.showError(it, false)
                },
                // onNext
                {
                    view.setData(it)
                    view.showContent()
                }
        )
    }

    override fun changeEnabled(source: SourceFilterPresentationModel) {

        val observable = sourceDao.enableSource(source.sourceId, !source.enabled).map { listOf(source) }

        subscribe(observable,
                {
                    view?.showError(it, true)
                },
                {

                },
                {},
                false) // Don't cancel when view gets destroyed


    }
}