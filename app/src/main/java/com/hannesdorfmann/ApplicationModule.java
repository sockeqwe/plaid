package com.hannesdorfmann;

import android.content.Context;
import com.hannesdorfmann.data.backend.BackendManager;
import com.hannesdorfmann.data.source.SourceDao;
import com.hannesdorfmann.data.source.SourceDaoImpl;
import com.hannesdorfmann.sqlbrite.dao.DaoManager;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import io.plaidapp.BuildConfig;
import io.plaidapp.data.api.AuthInterceptor;
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
  private SourceDao sourceDao;

  public ApplicationModule(Context context) {
    this.context = context.getApplicationContext();

    SourceDaoImpl sDao = new SourceDaoImpl();
    DaoManager manager = new DaoManager(this.context, "Sources", 1, sDao);
    manager.setLogging(true);

    sourceDao = sDao;
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
        .setRequestInterceptor(new AuthInterceptor(BuildConfig.DRIBBBLE_CLIENT_ACCESS_TOKEN))
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

  @Provides @Singleton public SourceDao provideSourceDao() {
    return sourceDao;
  }

  @Provides @Singleton public BackendManager backendManager() {
    return new BackendManager();
  }
}
