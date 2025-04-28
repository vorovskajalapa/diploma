package com.example.iot_ha

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.iot_ha.data.local.RoomLocalDatabase
import com.example.iot_ha.data.local.broker.Broker
import com.example.iot_ha.data.local.broker.BrokerDAO
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat

@RunWith(AndroidJUnit4::class)
class BrokerDAOTest {

    private lateinit var database: RoomLocalDatabase
    private lateinit var brokerDao: BrokerDAO

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            RoomLocalDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        brokerDao = database.brokerDAO()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertBroker_andGetAllBrokers() = runBlocking {
        val broker = Broker(
            serverUri = "tcp://example.com",
            serverPort = 1883,
            user = "user",
            password = "pass"
        )

        brokerDao.insert(broker)

        val brokers = brokerDao.getAllBrokers()

        assertThat(brokers).isNotEmpty()
        assertThat(brokers.first().serverUri).isEqualTo("tcp://example.com")
        assertThat(brokers.first().serverPort).isEqualTo(1883)
    }

    @Test
    fun insertMultipleBrokers_andGetLastBroker() = runBlocking {
        val broker1 = Broker(serverUri = "tcp://first.com", serverPort = 1883, user = null, password = null)
        val broker2 = Broker(serverUri = "tcp://second.com", serverPort = 1884, user = "admin", password = "adminpass")

        brokerDao.insert(broker1)
        brokerDao.insert(broker2)

        val lastBroker = brokerDao.getLastBroker()

        assertThat(lastBroker).isNotNull()
        assertThat(lastBroker?.serverUri).isEqualTo("tcp://second.com")
    }

    @Test
    fun insertAndDeleteBroker() = runBlocking {
        val broker = Broker(
            serverUri = "tcp://delete.com",
            serverPort = 1885,
            user = "delete",
            password = "deletepass"
        )

        brokerDao.insert(broker)

        val brokersBeforeDelete = brokerDao.getAllBrokers()
        assertThat(brokersBeforeDelete).hasSize(1)

        brokerDao.deleteBroker(brokersBeforeDelete.first())

        val brokersAfterDelete = brokerDao.getAllBrokers()
        assertThat(brokersAfterDelete).isEmpty()
    }
}
