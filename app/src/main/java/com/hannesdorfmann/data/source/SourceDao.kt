package com.hannesdorfmann.data.source

import rx.Observable

/**
 *
 * The SourceManager is responsible to save the sources
 *
 * @author Hannes Dorfmann
 */
interface SourceDao {

    /**
     * Get a list of all sources
     */
    fun getAllSources(): Observable<List<Source>>

    /**
     * Get a list of all enabled sources
     */
    fun getEnabledSources(): Observable<List<Source>>

    /**
     * Get a source by its unique id
     */
    fun getById(id: Long): Observable<Source?>

    /**
     * Add a source
     */
    fun insert(source: Source): Observable<Long>

    /**
     * Update a source
     */
    fun update(id: Long, order: Int, enabled: Boolean): Observable<Int>

    /**
     * Delete a source
     */
    fun delete(id: Long): Observable<Int>

    /**
     * Deletes all records
     */
    fun clear(): Observable<Int>

}
