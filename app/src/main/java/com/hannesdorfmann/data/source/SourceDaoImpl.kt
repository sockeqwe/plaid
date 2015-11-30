package com.hannesdorfmann.data.source

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.hannesdorfmann.data.backend.BackendManager
import com.hannesdorfmann.sqlbrite.dao.Dao
import io.plaidapp.R
import rx.Observable
import java.util.logging.Handler

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

    override fun createTable(database: SQLiteDatabase) {
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

        insertDefaultSources(database);
    }

    private fun insertDefaultSources(database: SQLiteDatabase) {


        val sources = arrayListOf(Source(Source.ID.DESIGNER_NEWS_POPULAR, R.string.source_designer_news_popular, 100, true, BackendManager.ID.DESIGNER_NEWS, false),
                Source(Source.ID.DESIGNER_NEWS_RECENT, R.string.source_designer_news_recent, 101, false, BackendManager.ID.DESIGNER_NEWS, false),
                Source(Source.ID.DRIBBBLE_POPULAR, R.string.source_dribbble_popular, 200, true, BackendManager.ID.DRIBBBLE, false),
                Source(Source.ID.DRIBBBLE_FOLLOWING, R.string.source_dribbble_following, 201, false, BackendManager.ID.DRIBBBLE, true),
                Source(Source.ID.DRIBBLE_MY_SHOTS, R.string.source_dribbble_user_shots, 202, false, BackendManager.ID.DRIBBBLE, true),
                Source(Source.ID.DRIBBLE_MY_LIKES, R.string.source_dribbble_user_likes, 203, false, BackendManager.ID.DRIBBBLE, true),
                Source(Source.ID.DRIBBLE_RECENT, R.string.source_dribbble_recent, 204, false, BackendManager.ID.DRIBBBLE, false),
                Source(Source.ID.DRIBBLE_DEBUTS, R.string.source_dribbble_debuts, 205, false, BackendManager.ID.DRIBBBLE, false),
                Source(Source.ID.DRIBBLE_ANIMATED, R.string.source_dribbble_animated, 206, true, BackendManager.ID.DRIBBBLE, false),
                Source(Source.ID.DRIBBLE_MATERIAL, R.string.source_dribbble_search_material_design, 207, false, BackendManager.ID.DRIBBBLE, false),
                Source(Source.ID.PRODUCT_HUNT, R.string.source_product_hunt, 300, true, BackendManager.ID.PRODUCT_HUNT, false)
        )

        sources.forEach {
            database.insert(TABLE, null, insertContentValues(it))
        }

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

    private fun insertContentValues(source: Source): ContentValues {


        val builder = SourceMapper.contentValues();
        // NO id set, so use sql AUTO_INCREMENT
        if (source.id != Source.ID.UNKNOWN_ID) {
            builder.id(source.id).enabled(source.enabled)
        }
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

        return cv
    }

    override fun insert(source: Source): Observable<Long> {

        val cv = insertContentValues(source)

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
        return defer { delete(TABLE, "${COL.ID} = ?", id.toString()) }
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