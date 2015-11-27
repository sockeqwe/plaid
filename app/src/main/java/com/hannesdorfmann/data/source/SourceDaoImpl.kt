package com.hannesdorfmann.data.source

import android.database.sqlite.SQLiteDatabase
import com.hannesdorfmann.sqlbrite.dao.Dao
import rx.Observable

/**
 *
 *
 * @author Hannes Dorfmann
 */
class SourceDaoImpl : Dao(), SourceDao {


    object COL {
        const val ID = "id"
        const val ORDER = "orderPosition"
        const val ENABLED = "enabled"
        const val AUTH_REQUIRED = "authRequired"
        const val BACKEND_ID = "backendId"
        const val NAME = "name"
        const val NAME_RES = "nameRes"
    }


    private val TABLE = "Source"

    override fun createTable(database: SQLiteDatabase?) {
        CREATE_TABLE(
                TABLE,
                "${COL.ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL",
                "${COL.ENABLED} BOOLEAN",
                "${COL.AUTH_REQUIRED} BOOLEAN",
                "${COL.ORDER} INTEGER",
                "${COL.BACKEND_ID} INTEGER NOT NULL",
                "${COL.NAME} TEXT",
                "${COL.NAME_RES} INTEGER")
                .execute(database)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    override fun getAllSources(): Observable<List<Source>> {
        return defer {
            query(
                    SELECT(COL.ID, COL.ENABLED, COL.ORDER, COL.AUTH_REQUIRED, COL.BACKEND_ID, COL.NAME, COL.NAME_RES)
                            .FROM(TABLE)
                            .ORDER_BY(COL.ORDER)
            ).run().mapToList(SourceMapper.MAPPER)
        }
    }

    override fun getEnabledSources(): Observable<List<Source>> {
        return defer {
            query(
                    SELECT(COL.ID, COL.ENABLED, COL.ORDER, COL.AUTH_REQUIRED, COL.BACKEND_ID, COL.NAME, COL.NAME_RES)
                            .FROM(TABLE)
                            .WHERE("${COL.ENABLED} = 1")
                            .ORDER_BY(COL.ORDER)
            ).run().mapToList(SourceMapper.MAPPER)
        }
    }

    override fun getSourcesForBackend(backendId: Int): Observable<List<Source>> {
        return defer {
            query(
                    SELECT(COL.ID, COL.ENABLED, COL.ORDER, COL.AUTH_REQUIRED, COL.BACKEND_ID, COL.NAME, COL.NAME_RES)
                            .FROM(TABLE)
                            .WHERE("${COL.BACKEND_ID} = ?")
            ).args(backendId.toString()).run().mapToList(SourceMapper.MAPPER)
        }
    }

    override fun getById(id: Long): Observable<Source?> {
        return defer {
            query(
                    SELECT(COL.ID, COL.ENABLED, COL.ORDER, COL.AUTH_REQUIRED, COL.BACKEND_ID, COL.NAME, COL.NAME_RES)
                            .FROM(TABLE)
                            .WHERE("${COL.ID} = ?")
            ).args(id.toString())
                    .run()
                    .mapToOneOrDefault(SourceMapper.MAPPER, null)
        }
    }

    override fun insert(source: Source): Observable<Long> {
        val builder = SourceMapper.contentValues();

        // NO id set, so use sql AUTO_INCREMENT
        if (source.id != Source.ID.UNKNOWN_ID) builder.id(source.id).enabled(source.enabled)
        if (source.name == null) {
            builder.nameAsNull()
        } else {
            builder.name(source.name)
        }

        val cv = builder.nameRes(source.nameRes)
                .order(source.order)
                .authenticationRequired(source.authenticationRequired)
                .backendId(source.backendId)
                .build()

        return defer { insert(TABLE, cv).doOnNext { source.id = it } } // set the id correctly in case of assigned by AUTO_INCREMENT
    }

    override fun update(id: Long, order: Int, enabled: Boolean): Observable<Int> {
        val cv = SourceMapper.contentValues()
                .order(order)
                .enabled(enabled)
                .build()

        return defer { update(TABLE, cv, "${COL.ID} = ?", id.toString()) }
    }

    override fun delete(id: Long): Observable<Int> {
        return defer {delete(TABLE, "${COL.ID} = ?", id.toString()) }
    }

    override fun clear(): Observable<Int> {
        return delete(TABLE)
    }

    override fun enableSource(sourceId: Long, enabled: Boolean): Observable<Int> {
        val cv = SourceMapper.contentValues()
                .enabled(enabled)
                .build()

        return defer { update(TABLE, cv, "${COL.ID} = ?", sourceId.toString()) }
    }

    /**
     * Little helper function to create a deferred observable
     */
    private fun  <T> defer(creation: () -> Observable<T>): Observable<T> {
        return Observable.defer {
            creation()
        }
    }
}