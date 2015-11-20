package com.hannesdorfmann.data.loader

import io.plaidapp.data.PlaidItem
import io.plaidapp.data.api.designernews.model.StoriesResponse

/**
 *
 *
 * @author Hannes Dorfmann
 */
fun extractPlaidItemsFromStory(story: StoriesResponse): List<PlaidItem> {
    return story.stories as List<PlaidItem>
}