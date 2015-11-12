package com.hannesdorfmann.data.source

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
class SourceDaoImplTest {

    var daoManager: DaoManager ? = null
    lateinit var dao: SourceDaoImpl

    @Before
    fun init() {
        if (daoManager == null) {
            // Workaround because we can not use @BeforeClass
            val context = RuntimeEnvironment.application
            dao = SourceDaoImpl()
            daoManager = DaoManager(context, "SourceTest.db", 1, dao)
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
        assertEmptyDatabase()
    }

    fun compareSource(a: Source, b: Source): Boolean {
        return a.id == b.id && a.order == b.order && a.enabled == b.enabled && a.authenticationRequired == b.authenticationRequired && a.backendId == b.backendId
    }

    fun assertEmptyDatabase() {
        Assert.assertTrue(dao.getAllSources().toBlocking().first().isEmpty())
    }

    @Test fun getById() {

        clearDatabase()

        val id = 42L
        val enabled = true
        val order = 23
        val authenticationRequired = true
        val backendId = 7

        val addSubscriber = TestSubscriber<Long>()

        val source = Source(id, order, enabled, backendId, authenticationRequired)
        dao.insert(source).subscribe(addSubscriber)

        addSubscriber.assertNoErrors()
        addSubscriber.assertCompleted()
        addSubscriber.assertValue(id)

        val queried = dao.getById(id).toBlocking().first()
        Assert.assertNotNull(queried)
        Assert.assertEquals(source, queried)
        Assert.assertTrue(compareSource(source, queried!!))

        // query not existing Not existing
        val notExisting = dao.getById(199).toBlocking().first()
        Assert.assertNull(notExisting)

    }

    @Test
    fun insertAndGet() {

        clearDatabase()

        // Insert and Get
        val sourceCount = 10L
        for (i in 0L..sourceCount) {
            val id = i
            val order = i.toInt()
            val enabled = i % 2L == 0L
            val backendId = i.toInt()
            val authenticated = enabled


            val addSubscriber = TestSubscriber<Long>()
            val toInsert = Source(id, order, enabled, backendId, authenticated)
            dao.insert(toInsert).subscribe(addSubscriber)

            addSubscriber.assertNoErrors()
            addSubscriber.assertCompleted()
            addSubscriber.assertValue(id)

            val queried = dao.getById(id).toBlocking().first();
            assertNotNull(queried)
            assertEquals(toInsert, queried)

            val allList = dao.getAllSources().toBlocking().first()
            assertNotNull(allList)
            assertEquals(i.toInt() + 1, allList.size)
            assertTrue(allList.contains(toInsert))
        }
    }

    @Test fun update() {

        clearDatabase()

        val id = 15L;
        val enabled = true
        val order = 23
        val authenticationRequired = true
        val backendId = 6

        val otherId = 4L
        val otherEnabled = true
        val otherOrder = 7
        val otherAuthenticationRequired = false
        val otherBackendId = 7

        // Insert
        val addSubscriber = TestSubscriber<Long>()
        val otherAddSubscriber = TestSubscriber<Long>()
        val original = Source(id, order, enabled, backendId, authenticationRequired)
        val other = Source(otherId, otherOrder, otherEnabled, otherBackendId, otherAuthenticationRequired)

        dao.insert(original).subscribe(addSubscriber)
        addSubscriber.assertNoErrors()
        addSubscriber.assertCompleted()
        addSubscriber.assertValue(id)

        dao.insert(other).subscribe(otherAddSubscriber)
        otherAddSubscriber.assertNoErrors()
        otherAddSubscriber.assertCompleted()
        otherAddSubscriber.assertValue(otherId)

        // Query
        assertNotNull(dao.getById(id).toBlocking().first())
        assertNotNull(dao.getById(otherId).toBlocking().first())

        // Update
        val uEnabled = false
        val uOrder = 12


        val updateSubscriber = TestSubscriber<Int>()
        dao.update(original.id, uOrder, uEnabled).subscribe(updateSubscriber)
        updateSubscriber.assertNoErrors()
        updateSubscriber.assertCompleted()
        updateSubscriber.assertValue(1)

        // Requery
        val requeriedOriginal = dao.getById(id).toBlocking().first()
        assertNotNull(requeriedOriginal)
        assertEquals(uOrder, requeriedOriginal!!.order)
        assertEquals(uEnabled, requeriedOriginal!!.enabled)
        assertEquals(backendId, requeriedOriginal!!.backendId) // BackendId not changeable
        assertEquals(authenticationRequired, requeriedOriginal!!.authenticationRequired) // authentication not changeable

        val requeriedOther = dao.getById(otherId).toBlocking().first()
        assertNotNull(requeriedOther)
        assertEquals(other, requeriedOther)
        assertTrue(compareSource(other, requeriedOther!!))

    }

    @Test fun delete() {
        clearDatabase()

        val id = 29L;
        val enabled = true
        val order = 23
        val authenticationRequired = true
        val backendId = 5

        val otherId = 4L
        val otherEnabled = true
        val otherOrder = 7
        val otherAuthenticationRequired = false
        val otherBackendId = 9

        // Insert
        val addSubscriber = TestSubscriber<Long>()
        val otherAddSubscriber = TestSubscriber<Long>()
        val original = Source(id, order, enabled, backendId, authenticationRequired)
        val other = Source(otherId, otherOrder, otherEnabled, otherBackendId, otherAuthenticationRequired)

        dao.insert(original).subscribe(addSubscriber)
        addSubscriber.assertNoErrors()
        addSubscriber.assertCompleted()
        addSubscriber.assertValue(id)

        dao.insert(other).subscribe(otherAddSubscriber)
        otherAddSubscriber.assertNoErrors()
        otherAddSubscriber.assertCompleted()
        otherAddSubscriber.assertValue(otherId)

        // Query
        assertNotNull(dao.getById(id).toBlocking().first())
        assertNotNull(dao.getById(otherId).toBlocking().first())

        // Delete
        val deleteSubscriber = TestSubscriber<Int>()
        dao.delete(id).subscribe(deleteSubscriber)
        deleteSubscriber.assertNoErrors()
        deleteSubscriber.assertCompleted()
        deleteSubscriber.assertValue(1)


        // Requery
        val requeriedOriginal = dao.getById(id).toBlocking().first()
        assertNull(requeriedOriginal)

        val requeriedOther = dao.getById(otherId).toBlocking().first()
        assertNotNull(requeriedOther)
        assertEquals(other, requeriedOther)
        assertTrue(compareSource(other, requeriedOther!!))
    }

    @Test fun getEnabled() {

        val enabledCount = 7
        val notEnabledCount = 3
        val enabledSources = ArrayList<Source>()
        val notEnabledSources = ArrayList<Source>()
        var id = 0L

        for (i in 1..enabledCount) {
            val addSubscriber = TestSubscriber<Long>()
            val source = Source(id, id.toInt(), true, 1, false)
            enabledSources.add(source)
            dao.insert(source).subscribe(addSubscriber)
            addSubscriber.assertNoErrors()
            addSubscriber.assertCompleted()
            addSubscriber.assertValue(id)
            id++
        }


        for (i in 1..notEnabledCount) {
            val addSubscriber = TestSubscriber<Long>()
            val source = Source(id, id.toInt(), false, 1, true)
            notEnabledSources.add(source)
            dao.insert(source).subscribe(addSubscriber)
            addSubscriber.assertNoErrors()
            addSubscriber.assertCompleted()
            addSubscriber.assertValue(id)
            id++
        }

        // Check all sources
        val all = dao.getAllSources().toBlocking().first()
        assertEquals(notEnabledCount + enabledCount, all.size)
        for (s in enabledSources) {
            assertTrue(all.contains(s))
        }

        for (s in notEnabledSources) {
            assertTrue(all.contains(s))
        }

        // Check enabled sources
        val enabeldOnly = dao.getEnabledSources().toBlocking().first()
        assertEquals(enabledCount, enabeldOnly.size)

        for (s in enabledSources) {
            assertTrue(enabeldOnly.contains(s))
        }

        for (s in notEnabledSources) {
            assertFalse(enabeldOnly.contains(s))
        }

    }

    @Test
    fun sourcesOfBackend() {
        clearDatabase()

        val sourcesCount = 10
        val backend1 = 1
        val backend2 = 2
        val backend1Sources = ArrayList<Source>()
        val backend2Sources = ArrayList<Source>()

        val enabledFun = fun(i: Int): Boolean {
            return i % 2 == 0
        }

        for (i in 0..sourcesCount - 1) {
            val s = Source(i.toLong(), i, enabledFun(i), backend1, true)
            dao.insert(s).toBlocking()
            backend1Sources.add(s)
        }


        for (i in sourcesCount..2 * sourcesCount - 1) {
            val s = Source(i.toLong(), i, enabledFun(i), backend2, true)
            dao.insert(s).toBlocking()
            backend2Sources.add(s)
        }


        val queriedBackend1 = dao.getSourcesForBackend(backend1.toLong()).toBlocking().first()
        assertEquals(queriedBackend1.size, sourcesCount)
        for (source in queriedBackend1) {
            val id = source.id
            assertEquals(id, source.order.toLong())
            assertEquals(enabledFun(id.toInt()), source.enabled)
            assertEquals(backend1, source.backendId)
            assertTrue(source.authenticationRequired)
            assertTrue(backend1Sources.remove(source))
        }

        assertTrue(backend1Sources.isEmpty())


        val queriedBackend2 = dao.getSourcesForBackend(backend2.toLong()).toBlocking().first()
        assertEquals(queriedBackend2.size, sourcesCount)
        for (source in queriedBackend2) {
            val id = source.id
            assertEquals(id, source.order.toLong())
            assertEquals(enabledFun(id.toInt()), source.enabled)
            assertEquals(backend2, source.backendId)
            assertTrue(source.authenticationRequired)
            assertTrue(backend2Sources.remove(source))
        }

        assertTrue(backend2Sources.isEmpty())


    }


}
