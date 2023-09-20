package com.example.appac.database

data class control(
    val `data`: DataControl
) {
    data class DataControl(
        val cloudToDeviceMessages: Int,
        val connectionState: String,
        val desired: String,
        val deviceToCloudMessages: Int,
        val enabled: String,
        val id: Int,
        val name: String,
        val reported: String
    )
}