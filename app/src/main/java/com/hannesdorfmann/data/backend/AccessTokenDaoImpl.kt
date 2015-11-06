package com.hannesdorfmann.data.backend

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.hannesdorfmann.sqlbrite.dao.Dao
import com.squareup.sqlbrite.BriteDatabase
import com.squareup.sqlbrite.QueryObservable
import rx.Observable

/**
 * Concrete SQL Database based implementation of AccessTokenDao
 *
 * @author Hannes Dorfmann
 */

class AccessTokenDaoImpl : Dao(), AccessTokenDao {

    companion object COL {
        const val BACKEND_ID = "backendId"
        const val TOKEN = "aToken"
    }

    private val TABLE = "AccessToken"

    override fun createTable(database: SQLiteDatabase?) {
        CREATE_TABLE(TABLE,
                "${BACKEND_ID} INTEGER PRIMARY KEY NOT NULL",
                "${TOKEN} TEXT NOT NULL"
        ).execute(database)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    override fun insertOrUpdate(backendId: Int, accessToken: String): Observable<Int> {
        val cv = ContentValues()
        cv.put(BACKEND_ID, backendId)
        cv.put(TOKEN, accessToken)
        return insert(TABLE, cv, SQLiteDatabase.CONFLICT_REPLACE).map { it.toInt() }
    }

    override fun delete(backendId: Int): Observable<Int> {
        return delete(TABLE, "${BACKEND_ID} = ?", backendId.toString())
    }

    override fun getAccessTokenForBackend(backendId: Int): Observable<String> {
        return query(
                SELECT(TOKEN).FROM(TABLE).WHERE("${BACKEND_ID} = ?"))
                .args(backendId.toString())
                .run()
                .mapToOneOrDefault({ cursor -> cursor.getString(0) }, null)

    }

    override fun clear(): Observable<Int> {
        return delete(TABLE)
    }
}
