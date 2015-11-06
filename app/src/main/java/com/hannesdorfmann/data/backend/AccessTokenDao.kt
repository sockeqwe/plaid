package com.hannesdorfmann.data.backend

import rx.Observable

/**
 * Dao responsible to manage access tokens
 * @author Hannes Dorfmann
 */
interface AccessTokenDao {

    /**
     * Add or update an access token
     */
    fun insertOrUpdate(backendId: Int, accessToken: String): Observable<Int>

    /**
     * Deletes an access token for the given Source
     */
    fun delete(backendId: Int): Observable<Int>

    /**
     * Get the AccessToken for the given Source
     */
    fun getAccessTokenForBackend(backendId: Int): Observable<String>

    /**
     * Delete all AccessTokens
     */
    fun clear(): Observable<Int>

}
