package com.hannesdorfmann.data.pager;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.internal.util.UtilityFunctions;
import rx.subjects.PublishSubject;
import rx.subscriptions.Subscriptions;

public class Pager<I, O> {

    private static final Observable FINISH_SEQUENCE = Observable.never();

    private PublishSubject<Observable<I>> pages;
    private Observable<I> nextPage = finish();
    private Subscription subscription = Subscriptions.empty();

    private final PagingFunction<I> pagingFunction;
    private final Func1<I, O> pageTransformer;

    public static <T> Pager<T, T> create(PagingFunction<T> pagingFunction) {
        return new Pager<>(pagingFunction, UtilityFunctions.<T>identity());
    }

    public static <I, O> Pager<I, O> create(PagingFunction<I> pagingFunction, Func1<I, O> pageTransformer) {
        return new Pager<>(pagingFunction, pageTransformer);
    }

    Pager(PagingFunction<I> pagingFunction, Func1<I, O> pageTransformer) {
        this.pagingFunction = pagingFunction;
        this.pageTransformer = pageTransformer;
    }

    /**
     * Used in the paging function to signal the caller that no more pages are available, i.e.
     * to finish paging by completing the paged sequence.
     *
     * @return the finish token
     */
    @SuppressWarnings("unchecked")
    public static <T> Observable<T> finish() {
        return FINISH_SEQUENCE;
    }

    /**
     * Transforms the given sequence to have its subsequent pages pushed into the observer subscribed
     * to the new sequence returned by this method. You can advance to the next page by calling {@link #next()}
     *
     * @param source the source sequence, which would be the first page of the sequence to be paged
     * @return a new sequence based on {@code source}, where subscribers keep receiving pages through subsequent calls
     * to {@link #next()}
     */
    public Observable<O> page(final Observable<I> source) {
        return Observable.create(new Observable.OnSubscribe<O>() {
            @Override
            public void call(final Subscriber<? super O> subscriber) {
                pages = PublishSubject.create();
                subscription = Observable.switchOnNext(pages).subscribe(new PageSubscriber(subscriber));
                subscriber.add(subscription);
                pages.onNext(source);
            }
        });
    }

    /**
     * Returns the last page received from the pager. You may use this to
     * retry that observable in case it failed the first time around.
     */
    public Observable<O> currentPage() {
        return page(nextPage);
    }

    /**
     * @return true, if there are more pages to be emitted.
     */
    public boolean hasNext() {
        return nextPage != FINISH_SEQUENCE;
    }

    /**
     * Advances the pager by pushing the next page of items into the current observer, is there is one. If the pager
     * has been unsubscribed from or there are no more pages, this method does nothing.
     */
    public void next() {
        if (!subscription.isUnsubscribed() && hasNext()) {
            pages.onNext(nextPage);
        }
    }

    public interface PagingFunction<T> extends Func1<T, Observable<T>> {
    }

    private final class PageSubscriber extends Subscriber<I> {
        private final Subscriber<? super O> inner;

        public PageSubscriber(Subscriber<? super O> inner) {
            this.inner = inner;
        }

        @Override
        public void onCompleted() {
            inner.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            inner.onError(e);
        }

        @Override
        public void onNext(I result) {
            nextPage = pagingFunction.call(result);
            inner.onNext(pageTransformer.call(result));
            if (nextPage == FINISH_SEQUENCE) {
                pages.onCompleted();
            }
        }
    }
}