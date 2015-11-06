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
        return a.id == b.id && a.order == b.order && a.enabled == b.enabled
    }

    fun assertEmptyDatabase() {
        Assert.assertTrue(dao.getAllSources().toBlocking().first().isEmpty())
    }

    @Test fun getById() {

        clearDatabase()

        val id = 42L
        val enabled = true
        val order = 23

        val addSubscriber = TestSubscriber<Long>()

        val source = Source(id, order, enabled)
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


            val addSubscriber = TestSubscriber<Long>()
            val toInsert = Source(id, order, enabled)
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

        val otherId = 4L
        val otherEnabled = true
        val otherOrder = 7

        // Insert
        val addSubscriber = TestSubscriber<Long>()
        val otherAddSubscriber = TestSubscriber<Long>()
        val original = Source(id, order, enabled)
        val other = Source(otherId, otherOrder, otherEnabled)

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

        val otherId = 4L
        val otherEnabled = true
        val otherOrder = 7

        // Insert
        val addSubscriber = TestSubscriber<Long>()
        val otherAddSubscriber = TestSubscriber<Long>()
        val original = Source(id, order, enabled)
        val other = Source(otherId, otherOrder, otherEnabled)

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
            val source = Source(id, id.toInt(), true)
            enabledSources.add(source)
            dao.insert(source).subscribe(addSubscriber)
            addSubscriber.assertNoErrors()
            addSubscriber.assertCompleted()
            addSubscriber.assertValue(id)
            id++
        }


        for (i in 1..notEnabledCount) {
            val addSubscriber = TestSubscriber<Long>()
            val source = Source(id, id.toInt(), false)
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

}
