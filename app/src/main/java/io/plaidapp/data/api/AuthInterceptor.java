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

package io.plaidapp.data.api;

import android.text.TextUtils;
import retrofit.RequestInterceptor;

/**
 * A {@see RequestInterceptor} that adds an auth token to requests
 */
public class AuthInterceptor implements RequestInterceptor {

  private String accessToken;

  public AuthInterceptor(String accessToken) {
    this.accessToken = accessToken;
  }

  @Override public void intercept(RequestFacade request) {
    if (!TextUtils.isEmpty(accessToken)) {
      request.addHeader("Authorization", "Bearer " + accessToken);
    }
  }

  private void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
}
