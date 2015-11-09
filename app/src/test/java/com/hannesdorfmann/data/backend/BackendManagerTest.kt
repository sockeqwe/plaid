package com.hannesdorfmann.data.backend

import io.plaidapp.data.api.designernews.DesignerNewsService
import io.plaidapp.data.api.dribbble.DribbbleService
import io.plaidapp.data.api.producthunt.ProductHuntService
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

/**
 *
 *
 * @author Hannes Dorfmann
 */
class BackendManagerTest {

    val dribbble = Mockito.mock(DribbbleService::class.java)
    val productHunt = Mockito.mock(ProductHuntService::class.java)
    val designerNews = Mockito.mock(DesignerNewsService::class.java)
    val manager = BackendManager(dribbble, productHunt, designerNews)

    @Test
    fun getBackendById() {
        assertEquals(dribbble, manager.getBackend(BackendManager.ID.DRIBBBLE))
        assertEquals(designerNews, manager.getBackend(BackendManager.ID.DESIGNER_NEWS))
        assertEquals(productHunt, manager.getBackend(BackendManager.ID.PRODUCT_HUNT))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getUnknownBackend() {
        manager.getBackend(9999) // Throws illegal Argument Exception
    }

}