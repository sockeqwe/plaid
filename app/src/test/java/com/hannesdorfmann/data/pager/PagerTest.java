package com.hannesdorfmann.data.pager;

import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

/**
 * @author Hannes Dorfmann
 */
public class PagerTest {

  @Test public void simpleTest() {

    final List<Integer> page1 = Arrays.asList(1, 2, 3);
    final List<Integer> page2 = Arrays.asList(4, 5, 6);

    // assuming a page type of `List<Integer>`, create your initial sequence
    Observable<List<Integer>> source = Observable.just(page1);

    // create the pager instance and provide the paging function
    Pager<List<Integer>, List<Integer>> pager =
        Pager.create(new Pager.PagingFunction<List<Integer>>() {
          public Observable<List<Integer>> call(List<Integer> previousPage) {
            // you need to define what it means to have "no more pages";
            // it could be the absence of a "next" link in a REST response, or no more
            // rows being read from a local database, or whatever you think you need.
            if (previousPage.get(0) == 4) {
              return Pager.finish();
            } else {
              // construct next page from previous page;
              // in a production impl this could be constructing a request Observable
              // by following a link in a REST API, but it typically involves looking at
              // `previousPage`
              return Observable.just(page2);
            }
          }
        });

    TestSubscriber<List<Integer>> subscriber = new TestSubscriber<>();

    // page your sequence; this will emit (1, 2, 3) to the subscriber right away
    pager.page(source).subscribe(subscriber);
    subscriber.assertNoErrors();
    subscriber.assertNotCompleted();
    Assert.assertEquals(1, subscriber.getOnNextEvents().size());
    Assert.assertEquals(page1, subscriber.getOnNextEvents().get(0));

    // this will emit (4, 5, 6)
    pager.next();
    subscriber.assertNoErrors();
    subscriber.assertCompleted();
    Assert.assertEquals(2, subscriber.getOnNextEvents().size());
    Assert.assertEquals(page1, subscriber.getOnNextEvents().get(0));
    Assert.assertEquals(page2, subscriber.getOnNextEvents().get(1));

  }
}
