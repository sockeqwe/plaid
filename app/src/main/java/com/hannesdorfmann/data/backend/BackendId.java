package com.hannesdorfmann.data.backend;

import android.support.annotation.IntDef;

/**
 * Intdef annotations for  {@link BackendManager.ID}
 *
 * @author Hannes Dorfmann
 */

@IntDef({
    BackendManager.ID.DESIGNER_NEWS, BackendManager.ID.DRIBBBLE, BackendManager.ID.PRODUCT_HUNT
}) public @interface BackendId {
}
