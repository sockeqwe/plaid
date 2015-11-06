package com.hannesdorfmann.data.source

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.hannesdorfmann.sqlbrite.dao.Dao
import com.squareup.sqlbrite.BriteDatabase
import rx.Observable

/**
 * Concrete SQL Database based implementation of AccessTokenDao
 *
 * @author Hannes Dorfmann
 */

class AccessTokenDaoImpl : Dao(), AccessTokenDao {

    companion object COL {
        const val SOURCE_ID = "sourceId"
        const val TOKEN = "aToken"
    }

    private val TABLE = "AccessToken"

    override fun createTable(database: SQLiteDatabase?) {
        CREATE_TABLE(TABLE,
                "${SOURCE_ID} INTEGER PRIMARY KEY NOT NULL",
                "${TOKEN} TEXT NOT NULL"
        ).execute(database)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    override fun insertOrUpdate(sourceId: Long, accessToken: String): Observable<Long> {
        val cv = ContentValues()
        cv.put(SOURCE_ID, sourceId)
        cv.put(TOKEN, accessToken)
        return insert(TABLE, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    override fun delete(sourceId: Long): Observable<Int> {
        return delete(TABLE, "${SOURCE_ID} = ?", sourceId.toString())
    }

    override fun getAccessTokenForSource(sourceId: Long): Observable<String> {
        return query(SELECT(TOKEN).FROM(TABLE).WHERE("${SOURCE_ID} = ?"))
                .args(sourceId.toString())
                .run()
                .mapToOneOrDefault({ cursor -> cursor.getString(0) }, null)
    }

    override fun clear(): Observable<Int> {
        return delete(TABLE)
    }
}
