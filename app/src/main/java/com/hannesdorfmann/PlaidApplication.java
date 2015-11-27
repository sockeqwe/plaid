package com.hannesdorfmann;

import android.app.Application;
import android.content.Context;
import android.support.annotation.VisibleForTesting;
import dagger.ObjectGraph;

/**
 * @author Hannes Dorfmann
 */
public class PlaidApplication extends Application {

  @VisibleForTesting private ObjectGraph objectGraph;

  @Override public void onCreate() {
    super.onCreate();

    objectGraph = ObjectGraph.create(new ApplicationModule(this));
  }

  public static ObjectGraph getObjectGraph(Context context) {
    return ((PlaidApplication) context.getApplicationContext()).objectGraph;
  }
}
