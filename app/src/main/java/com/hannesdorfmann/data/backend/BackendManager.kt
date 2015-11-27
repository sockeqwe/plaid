package com.hannesdorfmann.data.backend

import io.plaidapp.data.api.designernews.DesignerNewsService
import io.plaidapp.data.api.dribbble.DribbbleService
import io.plaidapp.data.api.producthunt.ProductHuntService

/**
 *
 *
 * @author Hannes Dorfmann
 */
class BackendManager(val dribbbleBackend: DribbbleService,
                     val productHuntBackend: ProductHuntService,
                     val designerNewsBackend: DesignerNewsService) {

    object ID {
        const val DRIBBBLE = 0
        const val DESIGNER_NEWS = 1
        const val PRODUCT_HUNT = 2
    }


    fun getBackend(@BackendId backendId: Int) = when (backendId) {
        ID.DRIBBBLE -> dribbbleBackend
        ID.DESIGNER_NEWS -> designerNewsBackend
        ID.PRODUCT_HUNT -> productHuntBackend
        else -> throw IllegalArgumentException("Unknown Backend for id = ${backendId}")
    }

    val getBackendIconRes = fun(@BackendId backendId: Int) = when (backendId) {
        ID.DRIBBBLE -> 1
        ID.DESIGNER_NEWS -> 2
        ID.PRODUCT_HUNT -> 3
        else -> throw IllegalArgumentException("Unknown Backend for id = ${backendId}")
    }
}