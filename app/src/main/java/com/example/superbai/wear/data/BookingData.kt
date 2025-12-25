package com.example.superbai.wear.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class BookingData(
    val id: String,
    val serviceName: String,
    val status: String,
    val maidName: String,
    val maidId: String,
    val timeSlot: String,
    val bookingDate: String,
    val todayStatus: String = "Not Started"
) {
    companion object {
        fun fromJson(json: String): BookingData? {
            return try {
                Gson().fromJson(json, BookingData::class.java)
            } catch (e: Exception) {
                null
            }
        }
        
        fun listFromJson(json: String): List<BookingData> {
            return try {
                val type = object : TypeToken<List<BookingData>>() {}.type
                Gson().fromJson(json, type)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    fun toJson(): String {
        return Gson().toJson(this)
    }
}
