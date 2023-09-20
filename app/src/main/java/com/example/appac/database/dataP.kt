package com.example.appac.database

data class dataP(
    val `data`: DataXXX
    ) {
        data class DataXXX(
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
