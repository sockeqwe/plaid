package com.hannesdorfmann.data.backend

import com.hannesdorfmann.data.backend.AccessTokenDaoImpl
import com.hannesdorfmann.sqlbrite.dao.DaoManager
import io.plaidapp.BuildConfig
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import rx.observers.TestSubscriber
import java.util.*
import kotlin.test.*

/**
 *
 *
 * @author Hannes Dorfmann
 */
@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(19))
class AccessTokenDaoTest {

    var daoManager: DaoManager ? = null
    lateinit var dao: AccessTokenDaoImpl

    @Before
    fun init() {
        if (daoManager == null) {
            // Workaround because we can not use @BeforeClass
            val context = RuntimeEnvironment.application
            dao = AccessTokenDaoImpl()
            daoManager = DaoManager(context, "AccessToken.db", 1, dao)
        }
    }

    /* @AfterClass
    fun cleanUp() {
        daoManager?.close()
        daoManager?.delete(RuntimeEnvironment.application)
    }
    */

    fun clearDatabase() {
        dao.clear()
    }

    @Test fun getByIdAndUpdate() {

        clearDatabase()

        val id = 42
        val token = "myToken"

        // Insert
        val insertSubscriber = TestSubscriber<Int>()
        dao.insertOrUpdate(id, token).subscribe(insertSubscriber)
        insertSubscriber.assertNoErrors()
        insertSubscriber.assertCompleted()
        insertSubscriber.assertValue(id)
        insertSubscriber.unsubscribe()

        val queriedToken = dao.getAccessTokenForBackend(id).toBlocking().first()
        assertEquals(token, queriedToken)


        // Update
        val updatedToken = "updatedToken"
        val updateSubscriber = TestSubscriber<Int>()
        dao.insertOrUpdate(id, updatedToken).subscribe(updateSubscriber)
        updateSubscriber.assertNoErrors()
        updateSubscriber.assertCompleted()
        updateSubscriber.assertValue(id)
        updateSubscriber.unsubscribe()

        val requeriedToken = dao.getAccessTokenForBackend(id).toBlocking().first()
        assertEquals(updatedToken, requeriedToken)


        // Query unknown
        val unknownToken = dao.getAccessTokenForBackend(345).toBlocking().first()
        assertNull(unknownToken)
    }

    @Test fun delete() {

        val id1 = 42
        val token1 = "firstToken"


        val id2 = 23
        val token2 = "secondToken"

        // Insert
        val insertSubscriber1 = TestSubscriber<Int>()
        dao.insertOrUpdate(id1, token1).subscribe(insertSubscriber1)
        insertSubscriber1.assertNoErrors()
        insertSubscriber1.assertCompleted()
        insertSubscriber1.assertValue(id1)
        insertSubscriber1.unsubscribe()

        val insertSubscriber2 = TestSubscriber<Int>()
        dao.insertOrUpdate(id2, token2).subscribe(insertSubscriber2)
        insertSubscriber2.assertNoErrors()
        insertSubscriber2.assertCompleted()
        insertSubscriber2.assertValue(id2)
        insertSubscriber2.unsubscribe()

        // Query
        var queriedToken1 = dao.getAccessTokenForBackend(id1).toBlocking().first()
        assertEquals(token1, queriedToken1)

        var queriedToken2 = dao.getAccessTokenForBackend(id2).toBlocking().first()
        assertEquals(token2, queriedToken2)

        // Delete
        val deleteSubscriber = TestSubscriber<Int>()
        dao.delete(id1).subscribe(deleteSubscriber)
        deleteSubscriber.assertNoErrors()
        deleteSubscriber.assertCompleted()
        deleteSubscriber.assertValue(1)

        queriedToken1 = dao.getAccessTokenForBackend(id1).toBlocking().first()
        assertNull(queriedToken1)

        queriedToken2 = dao.getAccessTokenForBackend(id2).toBlocking().first()
        assertEquals(token2, queriedToken2)

    }

}
