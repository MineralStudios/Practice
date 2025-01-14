package gg.mineral.practice.util.math

import kotlin.math.pow

object MathUtil {
    private const val DEFAULT_K_FACTOR = 25
    private const val WIN = 1
    private const val LOSS = 0

    fun getNewRating(rating: Int, opponentRating: Int, won: Boolean): Int {
        return getNewRating(rating, opponentRating, if (won) WIN else LOSS)
    }

    fun getNewRating(rating: Int, opponentRating: Int, score: Int): Int {
        val kFactor = getKFactor().toDouble()
        val expectedScore = getExpectedScore(rating, opponentRating)
        var newRating = calculateNewRating(rating, score, expectedScore, kFactor)

        return if (score == 1 && newRating == rating) ++newRating else newRating
    }

    private fun calculateNewRating(oldRating: Int, score: Int, expectedScore: Double, kFactor: Double): Int {
        return oldRating + (kFactor * (score - expectedScore)).toInt()
    }

    private fun getKFactor(): Float {
        return DEFAULT_K_FACTOR.toFloat()
    }

    private fun getExpectedScore(rating: Int, opponentRating: Int): Double {
        return 1 / (1 + 10.0.pow(((opponentRating - rating).toDouble() / 400)))
    }

    fun roundUp(`val`: Int, multiple: Int): Int {
        val mod = `val` % multiple
        return if (mod == 0) `val` else `val` + multiple - mod
    }

    fun roundUpToNearestWholeNumber(`val`: Double): Int {
        val intVal = `val`.toInt()
        return if (`val` - intVal == 0.0) intVal else intVal + 1
    }

    fun convertTicksToMinutes(ticks: Int): String {
        val minute = ticks / 1200L
        val second = ticks / 20L - minute * 60L
        var secondString = Math.round(second.toFloat()).toString() + ""
        if (second < 10L) {
            secondString = "0$secondString"
        }
        var minuteString = Math.round(minute.toFloat()).toString() + ""
        if (minute == 0L) {
            minuteString = "0"
        }
        return "$minuteString:$secondString"
    }

    fun convertToRomanNumeral(number: Int): String? {
        return when (number) {
            1 -> {
                "I"
            }

            2 -> {
                "II"
            }

            else -> {
                null
            }
        }
    }
}
