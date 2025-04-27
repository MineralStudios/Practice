package gg.mineral.practice.util

enum class DiscordTimestampFormat(private val code: String) {
    SHORT_TIME("t"),   // 1:34 AM
    LONG_TIME("T"),    // 1:34:00 AM
    SHORT_DATE("d"),   // 3/15/25
    LONG_DATE("D"),    // March 15, 2025
    SHORT_DATETIME("f"), // March 15, 2025 at 1:34 AM
    LONG_DATETIME("F"),  // Saturday, March 15, 2025 at 1:34 AM
    RELATIVE("R");    // 54 seconds ago

    fun formatTimestamp(epochSeconds: Long) = "<t:$epochSeconds:$code>"
}