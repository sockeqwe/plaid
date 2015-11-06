package com.hannesdorfmann.data.source;

import android.support.annotation.IntDef;

/**
 * Intdef annotations for  {@link SourceProvider}
 *
 * @author Hannes Dorfmann
 */

@IntDef({
    SourceProvider.DESIGNER_NEWS, SourceProvider.DRIBBBLE, SourceProvider.PRODUCT_HUNT
}) public @interface SourceProviderType {
}
