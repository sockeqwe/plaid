package com.hannesdorfmann.home;

import com.hannesdorfmann.ApplicationModule;
import com.hannesdorfmann.data.backend.paging.HomeDribbbleCallerFactory;
import com.hannesdorfmann.data.loader.router.RouteCallerFactory;
import com.hannesdorfmann.data.loader.router.Router;
import com.hannesdorfmann.data.news.ItemsLoader;
import com.hannesdorfmann.data.source.SourceDao;
import com.hannesdorfmann.scheduler.AndroidSchedulerTransformer;
import com.hannesdorfmann.scheduler.SchedulerTransformer;
import dagger.Module;
import dagger.Provides;
import io.plaidapp.data.PlaidItem;
import io.plaidapp.data.api.dribbble.DribbbleService;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Module(
    injects = {
        HomePresenter.class, HomePresenterImpl.class
    },
    addsTo = ApplicationModule.class,
    library = true)

public class HomeModule {

  @Provides @Singleton HomePresenter provideSearchPresenter(SourceDao sourceDao,
      DribbbleService dribbbleBackend) {

    SchedulerTransformer<List<? extends PlaidItem>> scheduler =
        new AndroidSchedulerTransformer<>();

    // Create the router
    List<RouteCallerFactory<List<? extends PlaidItem>>> routeCallerFactories = new ArrayList<>(3);
    routeCallerFactories.add(new HomeDribbbleCallerFactory(dribbbleBackend, sourceDao));

    Router<List<? extends PlaidItem>> router = new Router<>(routeCallerFactories);

    ItemsLoader<List<? extends PlaidItem>> itemsLoader = new ItemsLoader<>(router);

    return new HomePresenterImpl(itemsLoader, scheduler);
  }
}
