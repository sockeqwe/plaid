package com.hannesdorfmann.scheduler;

import rx.Observable;

/**
 * A {@link Observable.Transformer} that is used to set the schedulers for an observable / subscription
 */
public interface SchedulerTransformer<T> extends Observable.Transformer<T, T> {
}