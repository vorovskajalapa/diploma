package com.example.iot.data.local.device

class DeviceRepository(private val deviceDao: DeviceDAO) {
    suspend fun addDeviceIfNotExists(device: Device): Boolean {
        val existingDevice = deviceDao.getDeviceByIeeeAddr(device.ieeeAddr)
        return if (existingDevice == null) {
            val rowId = deviceDao.insertDevice(device)
            rowId != -1L
        } else {
            false
        }
    }
}