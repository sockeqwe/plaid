package com.hannesdorfmann.data.pager

import java.util.Arrays
import junit.framework.Assert
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber

/**
 * @author Hannes Dorfmann
 */
class PagerTest {

    @Test fun simpleTest() {

        val page1 = listOf(1, 2, 3)
        val page2 = listOf(4, 5, 6)

        // assuming a page type of `List<Integer>`, create your initial sequence
        val source = Observable.just(page1)

        val pager = Pager.create<List<Int>> { previousPage ->
            if (previousPage == page2)
            // End of the pager
                Pager.finish<List<Int>>()
            else
            // Next Page
                Observable.just(page2)
            
        }


        val subscriber = TestSubscriber<List<Int>>()

        // page your sequence; this will emit (1, 2, 3) to the subscriber right away
        pager.page(source).subscribe(subscriber)
        subscriber.assertNoErrors()
        subscriber.assertNotCompleted()
        Assert.assertEquals(1, subscriber.onNextEvents.size)
        Assert.assertEquals(page1, subscriber.onNextEvents[0])

        // this will emit (4, 5, 6)
        pager.next()
        subscriber.assertNoErrors()
        subscriber.assertCompleted()
        Assert.assertEquals(2, subscriber.onNextEvents.size)
        Assert.assertEquals(page1, subscriber.onNextEvents[0])
        Assert.assertEquals(page2, subscriber.onNextEvents[1])

    }
}
