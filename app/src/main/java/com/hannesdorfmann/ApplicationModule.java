package com.hannesdorfmann;

import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import io.plaidapp.data.api.designernews.DesignerNewsService;
import io.plaidapp.data.api.dribbble.DribbbleService;
import javax.inject.Singleton;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * @author Hannes Dorfmann
 */
@Module(
    library = true)
public class ApplicationModule {

  @Provides @Singleton OkHttpClient provideOkHttp() {
    return new OkHttpClient();
  }

  @Provides @Singleton DribbbleService provideDribbbleBackend(OkHttpClient okHttpClient) {
    RestAdapter adapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
        .setClient(new OkClient(okHttpClient))
        .setEndpoint(DribbbleService.ENDPOINT)
        .build();

    return adapter.create(DribbbleService.class);
  }

  @Provides @Singleton DesignerNewsService provideDesignerNewsBackend(OkHttpClient okHttpClient) {
    RestAdapter adapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
        .setClient(new OkClient(okHttpClient))
        .setEndpoint(DesignerNewsService.ENDPOINT)
        .build();

    return adapter.create(DesignerNewsService.class);
  }
}
