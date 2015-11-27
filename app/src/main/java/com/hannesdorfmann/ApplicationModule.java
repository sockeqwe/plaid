package com.hannesdorfmann;

import android.content.Context;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import io.plaidapp.data.api.designernews.DesignerNewsService;
import io.plaidapp.data.api.dribbble.DribbbleService;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * @author Hannes Dorfmann
 */
@Module(
    library = true) public class ApplicationModule {

  @Qualifier @Documented @Retention(RetentionPolicy.RUNTIME) public @interface ApplicationContext {
  }

  private Context context;

  public ApplicationModule(Context context) {
    this.context = context.getApplicationContext();
  }

  @Provides @Singleton @ApplicationContext public Context provideApplicationContext() {
    return context;
  }

  @Provides @Singleton public OkHttpClient provideOkHttp() {
    return new OkHttpClient();
  }

  @Provides @Singleton public DribbbleService provideDribbbleBackend(OkHttpClient okHttpClient) {
    RestAdapter adapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
        .setClient(new OkClient(okHttpClient))
        .setEndpoint(DribbbleService.ENDPOINT)
        .build();

    return adapter.create(DribbbleService.class);
  }

  @Provides @Singleton
  public DesignerNewsService provideDesignerNewsBackend(OkHttpClient okHttpClient) {
    RestAdapter adapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
        .setClient(new OkClient(okHttpClient))
        .setEndpoint(DesignerNewsService.ENDPOINT)
        .build();

    return adapter.create(DesignerNewsService.class);
  }
}
