package com.mobicom.s17.group8.mobicom_mco.utils

import com.google.api.client.util.DateTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Function to convert an RFC 3339 string to a formatted date string
fun String?.toFormattedDate(): String? {
    if (this == null) return null
    return try {
        val instant = Instant.parse(this)
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
        LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter)
    } catch (e: Exception) {
        // Return null or a fallback string if parsing fails
        null
    }
}

// Function to convert an RFC 3339 string to a formatted time string
fun String?.toFormattedTime(): String? {
    if (this == null) return null
    return try {
        val instant = Instant.parse(this)
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
        LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter)
    } catch (e: Exception) {
        null
    }
}