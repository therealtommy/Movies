package com.example.movies

import java.io.FileInputStream
import java.util.Properties

object Secrets {
    private const val PROPERTIES_FILE = "keystore.properties"
    private val properties: Properties = Properties()

    init {
        try {
            properties.load(FileInputStream(PROPERTIES_FILE))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getApiKey(): String? {
        return properties.getProperty("OMDB_API_KEY")
    }
}