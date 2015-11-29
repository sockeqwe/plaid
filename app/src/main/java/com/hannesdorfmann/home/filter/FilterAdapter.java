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

package com.hannesdorfmann.home.filter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import io.plaidapp.R;
import io.plaidapp.data.Source;
import io.plaidapp.data.prefs.SourceManager;
import io.plaidapp.ui.recyclerview.ItemTouchHelperAdapter;
import io.plaidapp.util.ColorUtils;
import io.plaidapp.util.ViewUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for showing the list of data sources used as filters for the home grid.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder>
    implements ItemTouchHelperAdapter {

  public interface FilterAuthoriser {
    void requestDribbbleAuthorisation(View sharedElement, Source forSource);
  }

  public interface SourceFilterClickedListener {
    void onSourceFilterClicked(@NonNull SourceFilterPresentationModel sourceFilter);
  }

  private static final int FILTER_ICON_ENABLED_ALPHA = 179; // 70%
  private static final int FILTER_ICON_DISABLED_ALPHA = 51; // 20%

  private List<SourceFilterPresentationModel> filters = new ArrayList<>();
  private SourceFilterClickedListener clickedListener;

  public FilterAdapter(@NonNull SourceFilterClickedListener clickedListener) {
    setHasStableIds(true);
    this.clickedListener = clickedListener;
  }

  public List<SourceFilterPresentationModel> getFilters() {
    return filters;
  }

  public void setFilters(List<SourceFilterPresentationModel> filters) {
    this.filters = filters;
  }

  @Override public FilterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    final FilterViewHolder holder = new FilterViewHolder(LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.filter_item, viewGroup, false));

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        final int position = holder.getAdapterPosition();
        if (position == RecyclerView.NO_POSITION) return;

        final SourceFilterPresentationModel filter = filters.get(position);
        holder.itemView.setHasTransientState(true);
        ObjectAnimator fade = ObjectAnimator.ofInt(holder.filterIcon, ViewUtils.IMAGE_ALPHA,
            filter.getEnabled() ? FILTER_ICON_DISABLED_ALPHA : FILTER_ICON_ENABLED_ALPHA);
        fade.setDuration(300);
        fade.setInterpolator(AnimationUtils.loadInterpolator(holder.itemView.getContext(),
            android.R.interpolator.fast_out_slow_in));
        fade.addListener(new AnimatorListenerAdapter() {
          @Override public void onAnimationEnd(Animator animation) {
            holder.itemView.setHasTransientState(false);
            clickedListener.onSourceFilterClicked(filter);
          }
        });
        fade.start();
      }
    });
    return holder;
  }

  @Override public void onBindViewHolder(final FilterViewHolder vh, int position) {
    final SourceFilterPresentationModel filter = filters.get(position);
    vh.filterName.setText(filter.getText());
    vh.filterName.setEnabled(filter.getEnabled());
    vh.filterIcon.setImageDrawable(vh.itemView.getContext().getDrawable(filter.getIconRes()));
    vh.filterIcon.setImageAlpha(
        filter.getEnabled() ? FILTER_ICON_ENABLED_ALPHA : FILTER_ICON_DISABLED_ALPHA);
  }

  @Override public int getItemCount() {
    return filters.size();
  }

  @Override public long getItemId(int position) {
    return filters.get(position).getSourceId();
  }

  private boolean isAuthorisedDribbbleSource(Source source) {
    return source.key.equals(SourceManager.SOURCE_DRIBBBLE_FOLLOWING) || source.key.equals(
        SourceManager.SOURCE_DRIBBBLE_USER_LIKES) || source.key.equals(
        SourceManager.SOURCE_DRIBBBLE_USER_SHOTS);
  }

  @Override public void onItemDismiss(int position) {
    // Not implemented yet
  }

  public static class FilterViewHolder extends RecyclerView.ViewHolder {

    public TextView filterName;
    public ImageView filterIcon;
    public boolean isSwipeable;

    public FilterViewHolder(View itemView) {
      super(itemView);
      filterName = (TextView) itemView.findViewById(R.id.filter_name);
      filterIcon = (ImageView) itemView.findViewById(R.id.filter_icon);
    }

    public void highlightFilter() {
      itemView.setHasTransientState(true);
      int highlightColor = ContextCompat.getColor(itemView.getContext(), R.color.accent);
      int fadeFromTo = ColorUtils.modifyAlpha(highlightColor, 0);
      ObjectAnimator background =
          ObjectAnimator.ofArgb(itemView, ViewUtils.BACKGROUND_COLOR, fadeFromTo, highlightColor,
              fadeFromTo);
      background.setDuration(1000L);
      background.setInterpolator(
          AnimationUtils.loadInterpolator(itemView.getContext(), android.R.interpolator.linear));
      background.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) {
          itemView.setBackground(null);
          itemView.setHasTransientState(false);
        }
      });
      background.start();
    }
  }
}
