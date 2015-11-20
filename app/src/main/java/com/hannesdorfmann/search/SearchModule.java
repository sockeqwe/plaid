package com.hannesdorfmann.search;

import com.hannesdorfmann.ApplicationModule;
import com.hannesdorfmann.data.loader.search.SearchItemsLoaderFactory;
import com.hannesdorfmann.scheduler.AndroidSchedulerTransformer;
import com.hannesdorfmann.scheduler.SchedulerTransformer;
import dagger.Module;
import dagger.Provides;
import io.plaidapp.data.PlaidItem;
import io.plaidapp.data.api.designernews.DesignerNewsService;
import java.util.List;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Module(
    injects = {
        SearchPresenterImpl.class, SearchItemsLoaderFactory.class
    },
    addsTo = ApplicationModule.class,
    library = true
)

public class SearchModule {

  @Provides @Singleton public SearchItemsLoaderFactory provideSearchItemsLoaderFactory(
      DesignerNewsService designerNewsBackend) {
    return new SearchItemsLoaderFactory(designerNewsBackend);
  }

  @Provides @Singleton SearchPresenter provideSearchPresenter(
      SearchItemsLoaderFactory itemsLoaderFactory) {

    SchedulerTransformer<List<? extends PlaidItem>> scheduler = new AndroidSchedulerTransformer<>();
    return new SearchPresenterImpl(itemsLoaderFactory, scheduler);
  }
}
