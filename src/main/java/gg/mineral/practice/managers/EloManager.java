package gg.mineral.practice.managers;

import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.eatthepath.uuid.FastUUID;

import gg.mineral.database.DatabaseAPIPlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.entity.ProfileData;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.collection.LeaderboardMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class EloManager {
	final static String TABLE = "elo";

	static {
		DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().ifPresent(sqlManager -> {
			sqlManager.executeStatement("CREATE TABLE IF NOT EXISTS " + TABLE
					+ " (ELO INT NOT NULL, PLAYER VARCHAR(200), GAMETYPE VARCHAR(200), UUID VARCHAR(200), UNIQUE(PLAYER, GAMETYPE, UUID))")
					.join();
		});
	}

	public static void update(ProfileData p, String g, int elo) {
		DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().ifPresent(sqlManager -> {
			sqlManager.executeStatement(
					"INSERT INTO " + TABLE + " (ELO, PLAYER, GAMETYPE, UUID) VALUES (?, ?, ?, ?) " +
							"ON DUPLICATE KEY UPDATE ELO=?, PLAYER=?",
					elo, p.getName(), g, p.getUuid().toString(),
					elo, p.getName());
		});
	}

	public static void updateName(Profile p) {
		DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().ifPresent(sqlManager -> {
			sqlManager.executeStatement(
					"INSERT INTO " + TABLE + " (PLAYER, UUID) VALUES (?, ?) " +
							"ON DUPLICATE KEY UPDATE PLAYER=?",
					p.getName(), p.getUuid().toString(),
					p.getName());
		});
	}

	public static CompletableFuture<Integer> get(Gametype gametype, UUID uuid) {

		if (DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().isPresent()) {
			return DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().get()
					.executeQuery("SELECT * FROM " + TABLE + " WHERE GAMETYPE=? AND UUID=?", gametype.getName(),
							uuid.toString())
					.thenApply(queryResult -> {
						Integer elo = 1000;
						try (ResultSet r = queryResult.getResultSet()) {

							if (r.next())
								elo = r.getInt("ELO");

						} catch (Exception e) {
							e.printStackTrace();
						}
						return elo;
					});
		}

		return CompletableFuture.completedFuture(1000);
	}

	public static CompletableFuture<Integer> get(Gametype gametype, String playerName) {

		if (DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().isPresent()) {
			return DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().get()
					.executeQuery("SELECT * FROM " + TABLE + " WHERE PLAYER=? AND GAMETYPE=?", playerName,
							gametype.getName())
					.thenApply(queryResult -> {
						Integer elo = 1000;
						try (ResultSet r = queryResult.getResultSet()) {
							if (r.next())
								elo = r.getInt("ELO");

						} catch (Exception e) {
							e.printStackTrace();
						}
						return elo;
					});
		}

		return CompletableFuture.completedFuture(1000);
	}

	public static void setAllEloAndLeaderboards() {
		if (DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().isPresent()) {
			DatabaseAPIPlugin.INSTANCE.retrieveSqlManager().get()
					.executeQuery("SELECT * FROM " + TABLE)
					.thenAccept(queryResult -> {
						try (ResultSet r = queryResult.getResultSet()) {
							while (r.next()) {
								String playerName = r.getString("PLAYER");
								int elo = r.getInt("ELO");
								String uuid = r.getString("UUID");
								String gametypeName = r.getString("GAMETYPE");
								Gametype gametype = GametypeManager.getGametypeByName(gametypeName);

								if (gametype == null)
									continue;

								gametype.getEloCache().put(
										ProfileManager.getProfileData(playerName, FastUUID.parseUUID(uuid)), elo);

								LeaderboardMap map = gametype.getLeaderboardMap(); // Fetch the existing LeaderboardMap
								map.put(playerName, elo);
								gametype.setLeaderboardMap(map);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}).join();
		}
	}

	public static LeaderboardMap getGlobalEloLeaderboard(Queuetype queuetype) {
		// The ordered map with all global elo that is returned
		LeaderboardMap map = new LeaderboardMap();

		// The map that will be used to store the elo sum <Player, Elo Sum>
		Object2IntOpenHashMap<ProfileData> globalEloMap = new Object2IntOpenHashMap<>();
		Object2IntOpenHashMap<ProfileData> divisorMap = new Object2IntOpenHashMap<>();

		// Iterates through every gametype in ranked, eg. NoDebuff, Debuff, Gapple
		// etc.....
		for (Gametype gametype : queuetype.getGametypes().keySet()) {

			// Iterate through entries and put the sum of elo in the map for each player
			for (Entry<ProfileData> e : gametype.getEloCache().object2IntEntrySet()) {
				// Get elo sum for this player, if it doesn't exist return 0
				Integer eloSum = globalEloMap.getOrDefault(e.getKey(), 0);
				Integer divisor = divisorMap.getOrDefault(e.getKey(), 0);

				// Add elo
				eloSum += e.getIntValue();

				// Update the map
				globalEloMap.put(e.getKey(), eloSum);
				divisorMap.put(e.getKey(), divisor + 1);

			}
		}

		int maxDivisor = queuetype.getGametypes().size();

		// Iterate through globalEloMap
		for (Entry<ProfileData> e : globalEloMap.object2IntEntrySet()) {
			int divisor = divisorMap.getOrDefault(e.getKey(), 0);

			if (divisor == 0) {
				map.put(e.getKey().getName(), 1000);
				continue;
			}

			int eloSum = e.getIntValue();

			if (divisor < maxDivisor)
				eloSum += 1000 * (maxDivisor - divisor);

			// Calculate global elo from the sum and divisor
			int globalElo = Math.round(eloSum / maxDivisor);
			// Put in leaderboard map where it is put into the correct order
			map.putNoDuplicate(e.getKey().getName(), globalElo);
		}

		return map;
	}
}
