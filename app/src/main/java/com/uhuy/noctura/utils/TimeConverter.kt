package com.uhuy.noctura.utils

class TimeConverter {
    /**
     * Converts a time string in "HH:mm" format to a float.
     * The calculation is: hour + (minute / 60.0)
     *
     * @param time The time string in "HH:mm" format.
     * @return The time as a float (hour + minute / 60).
     * @throws IllegalArgumentException if the time format is invalid.
     */
    companion object {
        fun convertTimeToFloat(time: String): Float {
            return try {
                // Split the input string by ":"
                val parts = time.split(":")

                // Parse hour and minute as integers
                val hour = parts[0].toInt()
                val minute = parts[1].toInt()

                // Convert to float using the formula
                hour + (minute / 60.0f)
            } catch (e: Exception) {
                // Handle invalid input format
                throw IllegalArgumentException("Invalid time format. Use HH:mm format.")
            }
        }
    }
}