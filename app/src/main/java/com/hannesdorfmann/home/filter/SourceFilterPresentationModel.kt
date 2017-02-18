package com.hannesdorfmann.home.filter

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.util.Log
import com.hannesdorfmann.data.source.Source
import java.util.*


data class SourceFilterPresentationModel(
        val sourceId: Long,
        val iconRes: Int,
        val text: String,
        val enabled: Boolean)

/**
 * Maps a list of [Source] to  a list of [SourceFilterPresentationModel]
 */
class SourceToPresentationModelMapper(private val context: Context, private val backendToIconMap: (Int) -> Int) {


    val mapperFunc = fun (sources: List<Source>): List<SourceFilterPresentationModel> {
        val presentationModels = ArrayList<SourceFilterPresentationModel>()
        Log.d("Test", "Mapping to presentation model: ${sources}")

        sources.forEach { source ->
            Log.d("Test", "Source mapping to presentation model: ${source}")
            
            val name =
                    if (source.name == null) {
                        context.resources.getString(source.nameRes)
                    } else {
                        source.name!!
                    }

            presentationModels.add(SourceFilterPresentationModel(source.id, backendToIconMap(source.backendId), name, source.enabled))
        }

        return presentationModels
    }
}
