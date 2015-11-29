/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.home;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowInsets;
import android.view.animation.AnimationUtils;
import android.widget.ActionMenuView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hannesdorfmann.FeedAdapter;
import com.hannesdorfmann.PlaidApplication;
import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import io.plaidapp.R;
import io.plaidapp.data.PlaidItem;
import io.plaidapp.data.Source;
import io.plaidapp.data.prefs.SourceManager;
import io.plaidapp.ui.HomeGridItemAnimator;
import com.hannesdorfmann.search.SearchActivity;
import io.plaidapp.ui.recyclerview.GridItemDividerDecoration;
import io.plaidapp.util.ViewUtils;
import java.security.InvalidParameterException;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class HomeActivity extends MvpViewStateActivity<HomeView, HomePresenter>
    implements HomeView {

  private static final int RC_SEARCH = 0;
  private static final int RC_AUTH_DRIBBBLE_FOLLOWING = 1;
  private static final int RC_AUTH_DRIBBBLE_USER_LIKES = 2;
  private static final int RC_AUTH_DRIBBBLE_USER_SHOTS = 3;
  private static final int RC_NEW_DESIGNER_NEWS_STORY = 4;
  private static final int RC_NEW_DESIGNER_NEWS_LOGIN = 5;

  @Bind(R.id.drawer) DrawerLayout drawer;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.stories_grid) RecyclerView grid;
  @Bind(R.id.fab) ImageButton fab;
  @Bind(android.R.id.empty) ProgressBar loading;
  private TextView noFiltersEmptyText;
  private GridLayoutManager layoutManager;
  private ImageView errorView;
  @BindInt(R.integer.num_columns) int columns;

  private FeedAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    setContentView(R.layout.activity_home2);
    ButterKnife.bind(this);

    drawer.setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

    //toolbar.inflateMenu(R.menu.main);
    setActionBar(toolbar);
    if (savedInstanceState == null) {
      animateToolbar();
    }

    adapter = new FeedAdapter(this, columns, false);
    grid.setAdapter(adapter);
    layoutManager = new GridLayoutManager(this, columns);
    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        return adapter.getItemColumnSpan(position);
      }
    });
    grid.setLayoutManager(layoutManager);
    grid.addOnScrollListener(gridScroll);
    grid.addOnScrollListener(new com.hannesdorfmann.InfiniteScrollListener(layoutManager) {
      @Override public void onLoadMore() {
        presenter.loadMore();
      }
    });
    grid.setHasFixedSize(true);
    grid.addItemDecoration(
        new GridItemDividerDecoration(adapter.getDividedViewHolderClasses(), this,
            R.dimen.divider_height, R.color.divider));
    grid.setItemAnimator(new HomeGridItemAnimator());

    // drawer layout treats fitsSystemWindows specially so we have to handle insets ourselves
    drawer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
      @Override public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
        // inset the toolbar down by the status bar height
        ViewGroup.MarginLayoutParams lpToolbar =
            (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
        lpToolbar.topMargin += insets.getSystemWindowInsetTop();
        lpToolbar.rightMargin += insets.getSystemWindowInsetRight();
        toolbar.setLayoutParams(lpToolbar);

        // inset the grid top by statusbar+toolbar & the bottom by the navbar (don't clip)
        grid.setPadding(grid.getPaddingLeft(),
            insets.getSystemWindowInsetTop() + ViewUtils.getActionBarSize(HomeActivity.this),
            grid.getPaddingRight() + insets.getSystemWindowInsetRight(), // landscape
            grid.getPaddingBottom());

        // inset the fab for the navbar
        ViewGroup.MarginLayoutParams lpFab = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
        lpFab.bottomMargin += insets.getSystemWindowInsetBottom(); // portrait
        lpFab.rightMargin += insets.getSystemWindowInsetRight(); // landscape
        fab.setLayoutParams(lpFab);

        // we place a background behind the status bar to combine with it's semi-transparent
        // color to get the desired appearance.  Set it's height to the status bar height
        View statusBarBackground = findViewById(R.id.status_bar_background);
        FrameLayout.LayoutParams lpStatus =
            (FrameLayout.LayoutParams) statusBarBackground.getLayoutParams();
        lpStatus.height = insets.getSystemWindowInsetTop();
        statusBarBackground.setLayoutParams(lpStatus);

        // clear this listener so insets aren't re-applied
        drawer.setOnApplyWindowInsetsListener(null);

        return insets.consumeSystemWindowInsets();
      }
    });
    setupTaskDescription();
  }

  private int gridScrollY = 0;
  private RecyclerView.OnScrollListener gridScroll = new RecyclerView.OnScrollListener() {
    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      gridScrollY += dy;
      if (gridScrollY > 0 && toolbar.getTranslationZ() != -1f) {
        toolbar.setTranslationZ(-1f);
      } else if (gridScrollY == 0 && toolbar.getTranslationZ() != 0) {
        toolbar.setTranslationZ(0f);
      }
    }
  };

  @OnClick(R.id.fab) protected void fabClick() {

  }

  private void setNoFiltersVisiblity(int visibility) {
    if (visibility == View.VISIBLE) {
      if (noFiltersEmptyText == null) {
        // create the no filters empty text
        ViewStub stub = (ViewStub) findViewById(R.id.stub_no_filters);
        noFiltersEmptyText = (TextView) stub.inflate();
        String emptyText = getString(R.string.no_filters_selected);
        int filterPlaceholderStart = emptyText.indexOf('\u08B4');
        int altMethodStart = filterPlaceholderStart + 3;
        SpannableStringBuilder ssb = new SpannableStringBuilder(emptyText);
        // show an image of the filter icon
        ssb.setSpan(new ImageSpan(this, R.drawable.ic_filter_small, ImageSpan.ALIGN_BASELINE),
            filterPlaceholderStart, filterPlaceholderStart + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // make the alt method (swipe from right) less prominent and italic
        ssb.setSpan(
            new ForegroundColorSpan(ContextCompat.getColor(this, R.color.text_secondary_light)),
            altMethodStart, emptyText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new StyleSpan(Typeface.ITALIC), altMethodStart, emptyText.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        noFiltersEmptyText.setText(ssb);
        noFiltersEmptyText.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            drawer.openDrawer(GravityCompat.END);
          }
        });
      }
      noFiltersEmptyText.setVisibility(visibility);
    } else if (noFiltersEmptyText != null) {
      noFiltersEmptyText.setVisibility(visibility);
    }
  }

  private void setupTaskDescription() {
    // set a silhouette icon in overview as the launcher icon is a bit busy
    // and looks bad on top of colorPrimary
    //Bitmap overviewIcon = ImageUtils.vectorToBitmap(this, R.drawable.ic_launcher_silhouette);
    // TODO replace launcher icon with a monochrome version from RN.
    Bitmap overviewIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    setTaskDescription(
        new ActivityManager.TaskDescription(getString(R.string.app_name), overviewIcon,
            ContextCompat.getColor(this, R.color.primary)));
    overviewIcon.recycle();
  }

  @Override protected void onResume() {
    super.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
  }

  private void animateToolbar() {
    // this is gross but toolbar doesn't expose it's children to animate them :(
    View t = toolbar.getChildAt(0);
    if (t != null && t instanceof TextView) {
      TextView title = (TextView) t;

      // fade in and space out the title.  Animating the letterSpacing performs horribly so
      // fake it by setting the desired letterSpacing then animating the scaleX ¯\_(ツ)_/¯
      title.setAlpha(0f);
      title.setScaleX(0.8f);

      title.animate()
          .alpha(1f)
          .scaleX(1f)
          .setStartDelay(300)
          .setDuration(900)
          .setInterpolator(
              AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in));
    }
    View amv = toolbar.getChildAt(1);
    if (amv != null & amv instanceof ActionMenuView) {
      ActionMenuView actions = (ActionMenuView) amv;
      popAnim(actions.getChildAt(0), 500, 200); // filter
      popAnim(actions.getChildAt(1), 700, 200); // overflow
    }
  }

  private void popAnim(View v, int startDelay, int duration) {
    if (v != null) {
      v.setAlpha(0f);
      v.setScaleX(0f);
      v.setScaleY(0f);

      v.animate()
          .alpha(1f)
          .scaleX(1f)
          .scaleY(1f)
          .setStartDelay(startDelay)
          .setDuration(duration)
          .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.overshoot));
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onPrepareOptionsMenu(Menu menu) {

    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_filter:
        drawer.openDrawer(GravityCompat.END);
        return true;
      case R.id.menu_search:
        // get the icon's location on screen to pass through to the search screen
        View searchMenuView = toolbar.findViewById(R.id.menu_search);
        int[] loc = new int[2];
        searchMenuView.getLocationOnScreen(loc);
        startActivityForResult(SearchActivity.createStartIntent(this, loc[0],
                loc[0] + (searchMenuView.getWidth() / 2)), RC_SEARCH,
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        searchMenuView.setAlpha(0f);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    /*
    switch (requestCode) {
      case RC_SEARCH:
        // reset the search icon which we hid
        View searchMenuView = toolbar.findViewById(R.id.menu_search);
        if (searchMenuView != null) {
          searchMenuView.setAlpha(1f);
        }
        if (resultCode == SearchActivity.RESULT_CODE_SAVE) {
          String query = data.getStringExtra(SearchActivity.EXTRA_QUERY);
          if (TextUtils.isEmpty(query)) return;
          Source dribbbleSearch = null;
          Source designerNewsSearch = null;
          boolean newSource = false;
          if (data.getBooleanExtra(SearchActivity.EXTRA_SAVE_DRIBBBLE, false)) {
            dribbbleSearch = new Source.DribbbleSearchSource(query, true);
            newSource |= filtersAdapter.addFilter(dribbbleSearch);
          }
          if (data.getBooleanExtra(SearchActivity.EXTRA_SAVE_DESIGNER_NEWS, false)) {
            designerNewsSearch = new Source.DesignerNewsSearchSource(query, true);
            newSource |= filtersAdapter.addFilter(designerNewsSearch);
          }
          if (newSource && (dribbbleSearch != null || designerNewsSearch != null)) {
            highlightNewSources(dribbbleSearch, designerNewsSearch);
          }
        }
        break;
      case RC_NEW_DESIGNER_NEWS_STORY:
        if (resultCode == PostNewDesignerNewsStory.RESULT_DRAG_DISMISSED) {
          // need to reshow the FAB as there's no shared element transition
          showFab();
        } else if (resultCode == PostNewDesignerNewsStory.RESULT_POST) {
          String title = data.getStringExtra(PostNewDesignerNewsStory.EXTRA_STORY_TITLE);
          String url = data.getStringExtra(PostNewDesignerNewsStory.EXTRA_STORY_URL);
          String comment = data.getStringExtra(PostNewDesignerNewsStory.EXTRA_STORY_COMMENT);
          if (!TextUtils.isEmpty(title)) {
            NewStoryRequest storyToPost = null;
            if (!TextUtils.isEmpty(url)) {
              storyToPost = NewStoryRequest.createWithUrl(title, url);
            } else if (!TextUtils.isEmpty(comment)) {
              storyToPost = NewStoryRequest.createWithComment(title, comment);
            }
            if (storyToPost != null) {

            }
          }
        }
        break;
      case RC_NEW_DESIGNER_NEWS_LOGIN:
        if (resultCode == RESULT_OK) {
          showFab();
        }
        break;
      case RC_AUTH_DRIBBBLE_FOLLOWING:
        if (resultCode == RESULT_OK) {
          filtersAdapter.enableFilterByKey(SourceManager.SOURCE_DRIBBBLE_FOLLOWING, this);
        }
        break;
      case RC_AUTH_DRIBBBLE_USER_LIKES:
        if (resultCode == RESULT_OK) {
          filtersAdapter.enableFilterByKey(SourceManager.SOURCE_DRIBBBLE_USER_LIKES, this);
        }
        break;
      case RC_AUTH_DRIBBBLE_USER_SHOTS:
        if (resultCode == RESULT_OK) {
          filtersAdapter.enableFilterByKey(SourceManager.SOURCE_DRIBBBLE_USER_SHOTS, this);
        }
        break;
    }
    */
  }

  private void showFab() {
    fab.setAlpha(0f);
    fab.setScaleX(0f);
    fab.setScaleY(0f);
    fab.setTranslationY(fab.getHeight() / 2);
    fab.animate()
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .translationY(0f)
        .setDuration(300L)
        .setInterpolator(
            AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in))
        .start();
  }

  /**
   * Highlight the new item by:
   * 1. opening the drawer
   * 2. scrolling it into view
   * 3. flashing it's background
   * 4. closing the drawer
   *
   * private void highlightNewSources(final Source... sources) {
   * final Runnable closeDrawerRunnable = new Runnable() {
   *
   * @Override public void run() {
   * drawer.closeDrawer(GravityCompat.END);
   * }
   * };
   * drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
   *
   * // if the user interacts with the filters while it's open then don't auto-close
   * private final View.OnTouchListener filtersTouch = new View.OnTouchListener() {
   * @Override public boolean onTouch(View v, MotionEvent event) {
   * drawer.removeCallbacks(closeDrawerRunnable);
   * return false;
   * }
   * };
   * @Override public void onDrawerOpened(View drawerView) {
   * // scroll to the new item(s) and highlight them
   * List<Integer> filterPositions = new ArrayList<>(sources.length);
   * for (Source source : sources) {
   * if (source != null) {
   * filterPositions.add(filtersAdapter.getFilterPosition(source));
   * }
   * }
   * int scrollTo = Collections.max(filterPositions);
   * filtersList.smoothScrollToPosition(scrollTo);
   * for (int position : filterPositions) {
   * FilterAdapter.FilterViewHolder holder =
   * (FilterAdapter.FilterViewHolder) filtersList.findViewHolderForAdapterPosition(
   * position);
   * if (holder != null) {
   * // this is failing for the first saved search, then working for subsequent calls
   * // TODO work out why!
   * holder.highlightFilter();
   * }
   * }
   * filtersList.setOnTouchListener(filtersTouch);
   * }
   * @Override public void onDrawerClosed(View drawerView) {
   * // reset
   * filtersList.setOnTouchListener(null);
   * drawer.setDrawerListener(null);
   * }
   * @Override public void onDrawerStateChanged(int newState) {
   * // if the user interacts with the drawer manually then don't auto-close
   * if (newState == DrawerLayout.STATE_DRAGGING) {
   * drawer.removeCallbacks(closeDrawerRunnable);
   * }
   * }
   * });
   * drawer.openDrawer(GravityCompat.END);
   * drawer.postDelayed(closeDrawerRunnable, 2000);
   * }
   */

  @Override public void onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.END)) {
      drawer.closeDrawer(GravityCompat.END);
    } else {
      super.onBackPressed();
    }
  }

  private int getAuthSourceRequestCode(Source filter) {
    switch (filter.key) {
      case SourceManager.SOURCE_DRIBBBLE_FOLLOWING:
        return RC_AUTH_DRIBBBLE_FOLLOWING;
      case SourceManager.SOURCE_DRIBBBLE_USER_LIKES:
        return RC_AUTH_DRIBBBLE_USER_LIKES;
      case SourceManager.SOURCE_DRIBBBLE_USER_SHOTS:
        return RC_AUTH_DRIBBBLE_USER_SHOTS;
    }
    throw new InvalidParameterException();
  }

  @Override public void onNewViewStateInstance() {

    // called first time the activity starts
    presenter.loadItems();
  }

  @Override public void showLoading() {

    loading.setVisibility(View.VISIBLE);
    grid.setVisibility(View.GONE);
    setNoFiltersVisiblity(View.GONE);

    castedViewState().setShowLoading();
  }

  @Override public void showContent() {

    if (!isRestoringViewState()) {
      // Crossfade
      grid.setAlpha(0f);
      grid.animate().alpha(1f).start();
      loading.setAlpha(1f);
      loading.animate().alpha(0f).start();
    }

    showFab();
    grid.setVisibility(View.VISIBLE);
    loading.setVisibility(View.GONE);
    setNoFiltersVisiblity(View.GONE);
    showErrorView(false);

    castedViewState().setShowContent(adapter.getItems());
  }

  @Override public void showError() {
    loading.setVisibility(View.GONE);
    grid.setVisibility(View.GONE);
    setNoFiltersVisiblity(View.GONE);
    showErrorView(true);

    castedViewState().setShowError();
  }

  private void showErrorView(boolean display) {

    if (display) {
      if (errorView == null) {
        ViewStub stub = (ViewStub) findViewById(R.id.stub_no_connection);
        errorView = (ImageView) stub.inflate();
        final AnimatedVectorDrawable avd =
            (AnimatedVectorDrawable) getDrawable(R.drawable.avd_no_connection);
        errorView.setImageDrawable(avd);
        avd.start();
      }
      errorView.setVisibility(View.VISIBLE);
    } else {
      if (errorView != null) {
        errorView.setVisibility(View.GONE);
      }
    }
  }

  @Override public void setContentItems(@NotNull List<? extends PlaidItem> items) {
    adapter.addAndResort(items);
  }

  @Override public void showLoadingMore(boolean showing) {
    adapter.setLoadingMore(showing);
    castedViewState().setShowLoadingMore(showing);
  }

  @Override public void showLoadingMoreError(@NotNull Throwable t) {
    Toast.makeText(this, R.string.error_more_search_results, Toast.LENGTH_LONG).show();
  }

  @Override public void addOlderItems(@NotNull List<? extends PlaidItem> items) {
    adapter.addAndResort(items);
  }

  @Override public ViewState<HomeView> createViewState() {
    return new HomeViewState();
  }

  private HomeViewState castedViewState() {
    return (HomeViewState) viewState;
  }

  @NonNull @Override public HomePresenter createPresenter() {
    return PlaidApplication.getObjectGraph(this).plus(new HomeModule()).get(HomePresenter.class);
  }

  @Override public void showNoFiltersSelected() {
    loading.setVisibility(View.GONE);
    grid.setVisibility(View.GONE);
    showErrorView(false);
    setNoFiltersVisiblity(View.VISIBLE);

    castedViewState().setShowNoFilters();
  }
}
