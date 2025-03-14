class DeviceActionBuilder(private val deviceId: Int) {
    fun buildOnToggle(): (Int, Boolean) -> Unit = { id, state ->
        if (id == deviceId) {
            println("[$deviceId] Toggle changed: $state")
        }
    }

    fun buildOnSliderChange(): (Int, Float) -> Unit = { id, value ->
        if (id == deviceId) {
            println("[$deviceId] Slider value: $value")
        }
    }

    fun buildOnSelectChange(): (Int, String) -> Unit = { id, option ->
        if (id == deviceId) {
            println("[$deviceId] Selected: $option")
        }
    }
}
