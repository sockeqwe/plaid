package com.hannesdorfmann.home.filter

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(io.plaidapp.R.layout.fragment_source_filter, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FilterAdapter(this)
        contentView.adapter = adapter
        contentView.layoutManager = LinearLayoutManager(activity)
    }

    override fun getData(): List<SourceFilterPresentationModel>? {
        return adapter.filters
    }

    override fun createViewState(): LceViewState<List<SourceFilterPresentationModel>, SourceFilterView>? {
        return RetainingLceViewState()
    }

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String? {
        return resources.getString(io.plaidapp.R.string.error_database)
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