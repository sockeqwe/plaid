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


    companion object COL {
        const val ID = "id"
        const val ORDER = "orderPosition"
        const val ENABLED = "enabled"
        const val AUTH_REQUIRED = "authRequired"
    }


    private val TABLE = "Source"

    override fun createTable(database: SQLiteDatabase?) {
        CREATE_TABLE(
                TABLE,
                "${COL.ID} INTEGER PRIMARY KEY NOT NULL",
                "${COL.ENABLED} BOOLEAN",
                "${COL.AUTH_REQUIRED} BOOLEAN",
                "${COL.ORDER} INTEGER")
                .execute(database)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    override fun getAllSources(): Observable<List<Source>> {
        return query(
                SELECT(COL.ID, COL.ENABLED, COL.ORDER)
                        .FROM(TABLE)
                        .ORDER_BY(COL.ORDER)
        ).run().mapToList(SourceMapper.MAPPER)
    }

    override fun getEnabledSources(): Observable<List<Source>> {
        return query(
                SELECT(COL.ID, COL.ENABLED, COL.ORDER)
                        .FROM(TABLE).WHERE("${COL.ENABLED} = 1")
                        .ORDER_BY(COL.ORDER)
        ).run().mapToList(SourceMapper.MAPPER)
    }

    override fun getById(id: Long): Observable<Source?> {
        return query(
                SELECT(COL.ID, COL.ENABLED, COL.ORDER)
                        .FROM(TABLE)
                        .WHERE("${COL.ID} = ?")
        ).args(id.toString())
                .run()
                .mapToOneOrDefault(SourceMapper.MAPPER, null)
    }

    override fun insert(source: Source): Observable<Long> {
        val cv = SourceMapper.contentValues()
                .id(source.id).enabled(source.enabled)
                .order(source.order)
                .build()
        return insert(TABLE, cv)
    }

    override fun update(id: Long, order: Int, enabled: Boolean): Observable<Int> {
        val cv = SourceMapper.contentValues()
                .order(order)
                .enabled(enabled)
                .build()

        return update(TABLE, cv, "${COL.ID} = ?", id.toString())
    }

    override fun delete(id: Long): Observable<Int> {
        return delete(TABLE, "${COL.ID} = ?", id.toString())
    }

    override fun clear(): Observable<Int> {
        return delete(TABLE)
    }
}