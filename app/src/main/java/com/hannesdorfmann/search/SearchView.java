package com.hannesdorfmann.search;

import com.hannesdorfmann.mosby.mvp.MvpView;
import io.plaidapp.data.PlaidItem;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public interface SearchView extends MvpView{

  void showLoading();

  void showContent();

  void showError(Throwable t);

  void setContentItems(List<PlaidItem> items);

  void showLoadingMore(boolean showing);

  void showLoadingMoreError(Throwable t);

  void addOlderItems(List<PlaidItem> items);

  void showSearchNotStarted();
}
