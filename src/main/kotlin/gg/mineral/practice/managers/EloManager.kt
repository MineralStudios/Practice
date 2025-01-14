package gg.mineral.practice.managers

import com.eatthepath.uuid.FastUUID
import gg.mineral.database.DatabaseAPIPlugin
import gg.mineral.database.sql.QueryResult
import gg.mineral.database.sql.SQLManager
import gg.mineral.practice.entity.ExtendedProfileData
import gg.mineral.practice.entity.ProfileData
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.managers.GametypeManager.getGametypeByName
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.util.collection.LeaderboardMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

object EloManager {
    private const val TABLE: String = "elo"

    init {
        DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().ifPresent { sqlManager: SQLManager ->
            sqlManager.executeStatement(
                ("CREATE TABLE IF NOT EXISTS " + TABLE
                        + " (ELO INT NOT NULL, PLAYER VARCHAR(200), GAMETYPE VARCHAR(200), UUID VARCHAR(200), UNIQUE(PLAYER, GAMETYPE, UUID))")
            )
                .join()
        }
    }

    fun update(p: ExtendedProfileData, g: String?, elo: Int) {
        DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().ifPresent { sqlManager: SQLManager ->
            sqlManager.executeStatement(
                "INSERT INTO " + TABLE + " (ELO, PLAYER, GAMETYPE, UUID) VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE ELO=?, PLAYER=?",
                elo, p.name, g, p.uuid.toString(),
                elo, p.name
            )
        }
    }

    @JvmStatic
    fun updateName(p: Player) {
        DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().ifPresent { sqlManager: SQLManager ->
            sqlManager.executeStatement(
                "INSERT INTO " + TABLE + " (PLAYER, UUID) VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE PLAYER=?",
                p.name, p.uniqueId.toString(),
                p.name
            )
        }
    }

    fun get(gametype: Gametype, uuid: UUID): CompletableFuture<Int> {
        if (DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().isPresent) {
            return DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().get()
                .executeQuery(
                    "SELECT * FROM " + TABLE + " WHERE GAMETYPE=? AND UUID=?", gametype.name,
                    uuid.toString()
                )
                .thenApply { queryResult: QueryResult ->
                    var elo = 1000
                    try {
                        queryResult.resultSet.use { r ->
                            if (r.next()) elo = r.getInt("ELO")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    elo
                }
        }

        return CompletableFuture.completedFuture(1000)
    }

    fun get(gametype: Gametype, playerName: String?): CompletableFuture<Int> {
        if (DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().isPresent) {
            return DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().get()
                .executeQuery(
                    "SELECT * FROM " + TABLE + " WHERE PLAYER=? AND GAMETYPE=?", playerName,
                    gametype.name
                )
                .thenApply { queryResult: QueryResult ->
                    var elo = 1000
                    try {
                        queryResult.resultSet.use { r ->
                            if (r.next()) elo = r.getInt("ELO")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    elo
                }
        }

        return CompletableFuture.completedFuture(1000)
    }

    fun setAllEloAndLeaderboards() {
        if (DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().isPresent) {
            DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().get()
                .executeQuery("SELECT * FROM " + TABLE)
                .thenAccept { queryResult: QueryResult ->
                    try {
                        queryResult.resultSet.use { r ->
                            while (r.next()) {
                                val playerName = r.getString("PLAYER")
                                val elo = r.getInt("ELO")
                                val uuid = r.getString("UUID")
                                val gametypeName = r.getString("GAMETYPE")
                                val gametype = getGametypeByName(gametypeName) ?: continue

                                gametype.eloCache.put(
                                    ProfileManager.getProfileData(playerName, FastUUID.parseUUID(uuid)), elo
                                )

                                gametype.leaderboardMap.put(playerName, elo)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.join()
        }
    }

    fun getGlobalEloLeaderboard(queuetype: Queuetype): LeaderboardMap {
        // The ordered map with all global elo that is returned
        val map = LeaderboardMap()

        // The map that will be used to store the elo sum and divisor <Player, Elo Sum
        // and Divisor>
        val globalEloMap = Object2LongOpenHashMap<ProfileData>()

        var maxDivisor = 0

        // Iterates through every gametype in ranked, eg. NoDebuff, Debuff, Gapple etc.
        for (menuEntry in queuetype.menuEntries.keys) {
            if (menuEntry is Gametype) {
                maxDivisor++

                // Iterate through entries and put the sum of elo in the map for each player
                for (e in menuEntry.eloCache.object2IntEntrySet()) {
                    // Get the combined value (eloSum and divisor)
                    val combinedValue: Long = globalEloMap.getLong(e.key)
                    var eloSum = (combinedValue ushr 32).toInt()
                    var divisor = combinedValue.toInt()

                    // Add elo
                    eloSum += e.intValue
                    divisor += 1

                    // Update the map with the new combined value
                    globalEloMap.put(e.key, ((eloSum.toLong()) shl 32) or (divisor.toLong() and 0xFFFFFFFFL))
                }
            }
        }

        // Iterate through globalEloMap
        for (e in globalEloMap.object2LongEntrySet()) {
            val combinedValue = e.longValue
            var eloSum = (combinedValue ushr 32).toInt()
            val divisor = combinedValue.toInt()

            if (divisor == 0) {
                map.put(e.key.name, 1000)
                continue
            }

            if (divisor < maxDivisor) eloSum += 1000 * (maxDivisor - divisor)

            // Calculate global elo from the sum and divisor
            val globalElo = Math.round(eloSum.toFloat() / maxDivisor)
            // Put in leaderboard map where it is put into the correct order
            map.putNoDuplicate(e.key.name, globalElo)
        }

        return map
    }
}
