package com.hannesdorfmann.data.backend

import io.plaidapp.R
import io.plaidapp.data.api.designernews.DesignerNewsService
import io.plaidapp.data.api.dribbble.DribbbleService
import io.plaidapp.data.api.producthunt.ProductHuntService

/**
 *
 *
 * @author Hannes Dorfmann
 */
class BackendManager {

    object ID {
        const val DRIBBBLE = 0
        const val DESIGNER_NEWS = 1
        const val PRODUCT_HUNT = 2
    }

    val getBackendIconRes = fun(@BackendId backendId: Int) = when (backendId) {
        ID.DRIBBBLE -> R.drawable.ic_dribbble
        ID.DESIGNER_NEWS -> R.drawable.ic_designer_news
        ID.PRODUCT_HUNT -> R.drawable.ic_product_hunt
        else -> throw IllegalArgumentException("Unknown Backend for id = ${backendId}")
    }
}