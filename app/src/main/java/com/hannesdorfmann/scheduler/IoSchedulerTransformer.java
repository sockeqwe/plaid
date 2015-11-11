package com.hannesdorfmann.scheduler;

import rx.Observable;
import rx.schedulers.Schedulers;

public class IoSchedulerTransformer<T> implements SchedulerTransformer<T> {

  @Override public Observable<T> call(Observable<T> observable) {
    return observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
  }
}