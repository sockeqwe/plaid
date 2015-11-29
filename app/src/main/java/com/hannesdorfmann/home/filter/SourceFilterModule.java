package com.hannesdorfmann.home.filter;

import android.content.Context;
import com.hannesdorfmann.ApplicationModule;
import com.hannesdorfmann.data.backend.BackendManager;
import com.hannesdorfmann.data.source.SourceDao;
import com.hannesdorfmann.scheduler.AndroidSchedulerTransformer;
import com.hannesdorfmann.scheduler.SchedulerTransformer;
import dagger.Module;
import dagger.Provides;
import java.util.List;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Module(
    injects = {
        SourceFilterPresenter.class, SourceFilterPresenterImpl.class
    },
    addsTo = ApplicationModule.class,
    library = true)

public class SourceFilterModule {

  @Provides @Singleton SourceFilterPresenter provideSearchPresenter(
      @ApplicationModule.ApplicationContext Context context, SourceDao sourceDao,
      BackendManager backendManager) {

    SourceToPresentationModelMapper presentationModelMapper =
        new SourceToPresentationModelMapper(context.getApplicationContext(),
            backendManager.getGetBackendIconRes());

    SchedulerTransformer<List<? extends SourceFilterPresentationModel>> scheduler =
        new AndroidSchedulerTransformer<>();
    return new SourceFilterPresenterImpl(sourceDao, presentationModelMapper, scheduler);
  }
}
