package com.hannesdorfmann.home;

import com.hannesdorfmann.ApplicationModule;
import com.hannesdorfmann.data.loader.home.HomeDribbbleCallerFactory;
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

    SchedulerTransformer<List<PlaidItem>> scheduler =
        new AndroidSchedulerTransformer<>();

    HomeDribbbleCallerFactory homeDribbbleCallerFactory =
        new HomeDribbbleCallerFactory(dribbbleBackend, sourceDao);

    // Create the router
    List<RouteCallerFactory<List<PlaidItem>>> routeCallerFactories = new ArrayList<>(3);
    //routeCallerFactories.add();

    Router<List<PlaidItem>> router = new Router<>(routeCallerFactories);

    ItemsLoader<List<PlaidItem>> itemsLoader = new ItemsLoader<>(router);

    return new HomePresenterImpl(itemsLoader, scheduler);
  }
}
