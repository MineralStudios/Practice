package gg.mineral.practice.tournaments

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.DiscordTimestampFormat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.bukkit.Bukkit

class AutomatedTournament(hostName: String, override val waitTime: Int = 60 * 15) :
    Tournament(hostName, waitTime) {
    private val client = OkHttpClient()
    val reward: Rank = Rank.entries.random()

    private fun sendDiscordEmbed(embed: JsonObject, tag: Boolean = true) {

        val payload = JsonObject().apply {
            addProperty("username", "Mineral Events")
            addProperty(
                "avatar_url",
                "https://cdn.discordapp.com/avatars/1347016036079112213/dcd2dd7d4c8385d22073267b2f6e6d9d.png"
            )
            add("embeds", JsonArray().apply { add(embed) })
            if (tag)
                addProperty("content", "<@&1350294301871177793>")
        }

        val body = payload.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(PracticePlugin.INSTANCE.discordWebhook)
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful)
                println("Failed to send embed: ${response.code} ${response.message}")
        }
    }

    private fun createEmbed(time: Long? = null): JsonObject {
        return JsonObject().apply {
            addProperty("title", "TOURNAMENT")
            addProperty(
                "description", """
            **Mode: ${matchData.gametype?.name ?: "Custom"}** 
            - **Server:** NA Practice ([mineral.gg](https://mineral.gg))
            - **Time:** ${time?.let { DiscordTimestampFormat.RELATIVE.formatTimestamp(it) } ?: "N/A"}
            - **Prize:** ${reward.rank} Rank (7 days)
        """.trimIndent()
            )
            addProperty("color", 0x3344db)
        }
    }

    private fun createWinnerEmbed(winner: Profile): JsonObject {
        return JsonObject().apply {
            addProperty("title", "WINNER: ${winner.name}")
            addProperty(
                "description", """
            - **Prize:** ${reward.rank} Rank (7 days)
        """.trimIndent()
            )
            addProperty("color", 0x00FF00)
        }
    }

    override fun startContest() {
        super.startContest()
        Bukkit.getScheduler().runTaskAsynchronously(PracticePlugin.INSTANCE) {
            val embed = createEmbed(
                time = (System.currentTimeMillis() + (waitTime * 1000)) / 1000
            )
            sendDiscordEmbed(embed)
        }
    }

    override fun onContestWin(winner: Profile?) {
        super.onContestWin(winner)

        winner?.let {
            val embed = createWinnerEmbed(it)
            Bukkit.getScheduler().runTaskAsynchronously(PracticePlugin.INSTANCE) {
                sendDiscordEmbed(embed, false)
            }
            //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "grant ${reward.rank} ${it.name} 7d")
        }
    }

    enum class Rank(val rank: String) {
        MINERAL("Mineral"),
        GOLD("Gold"),
        SILVER("Silver"),
    }
}
