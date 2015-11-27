package com.hannesdorfmann.home.sources

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hannesdorfmann.PlaidApplication
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState
import com.hannesdorfmann.search.SearchModule
import dagger.ObjectGraph

/**
 *
 *
 * @author Hannes Dorfmann
 */
class SourceFilterFragment : SourceFilterView, MvpLceViewStateFragment<RecyclerView, List<SourceFilterPresentationModel>, SourceFilterView, SourceFilterPresenter>(), FilterAdapter.SourceFilterClickedListener {

    lateinit var adapter: FilterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FilterAdapter(this)
    }

    override fun getData(): List<SourceFilterPresentationModel>? {
        return adapter.filters
    }

    override fun createViewState(): LceViewState<List<SourceFilterPresentationModel>, SourceFilterView>? {
        return RetainingLceViewState()
    }

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String? {
        return resources.getString(1)
    }

    override fun loadData(pullToRefresh: Boolean) {
        presenter.loadSources()
    }

    override fun setData(data: List<SourceFilterPresentationModel>?) {
        adapter.filters = data
        adapter.notifyDataSetChanged()
    }

    override fun createPresenter(): SourceFilterPresenter {
        return PlaidApplication.getObjectGraph(context).plus(SourceFilterModule()).get(SourceFilterPresenter::class.java)
    }

    override fun onSourceFilterClicked(sourceFilter: SourceFilterPresentationModel) {
        presenter.changeEnabled(sourceFilter)
    }
}