package com.hannesdorfmann.data.source

import rx.Observable

/**
 * Dao responsible to manage access tokens
 * @author Hannes Dorfmann
 */
interface AccessTokenDao {

    /**
     * Add or update an access token
     */
    fun insertOrUpdate(sourceId: Long, accessToken: String): Observable<Long>

    /**
     * Deletes an access token for the given Source
     */
    fun delete(sourceId: Long): Observable<Int>

    /**
     * Get the AccessToken for the given Source
     */
    fun getAccessTokenForSource(sourceId: Long): Observable<String>

    /**
     * Delete all AccessTokens
     */
    fun clear(): Observable<Int>

}
