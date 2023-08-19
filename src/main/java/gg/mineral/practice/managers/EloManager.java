package gg.mineral.practice.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.sql.SQLManager;
import gg.mineral.practice.util.collection.LeaderboardMap;

public class EloManager {
	final static String TABLE = "elo";

	static {
		if (PracticePlugin.DB_CONNECTED) {

			try {
				AutoCloseable[] stmt = SQLManager.prepare("CREATE TABLE IF NOT EXISTS " + TABLE
						+ " (ELO INT NOT NULL, PLAYER VARCHAR(200), GAMETYPE VARCHAR(200), UUID VARCHAR(200))");
				SQLManager.execute(stmt);
				SQLManager.close(stmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static CompletableFuture<Void> update(Profile p, String g, int elo) {

		return CompletableFuture.runAsync(() -> {
			if (!PracticePlugin.DB_CONNECTED)
				return;
			try {
				AutoCloseable[] statement = SQLManager
						.prepare(exists(p.getUUID().toString(), g)
								? "UPDATE " + TABLE + " SET ELO=?, PLAYER=? WHERE GAMETYPE=? AND UUID=?"
								: "INSERT INTO " + TABLE + " (ELO, PLAYER, GAMETYPE, UUID) VALUES (?, ?, ?, ?)");
				PreparedStatement stmt = (PreparedStatement) statement[0];
				stmt.setInt(1, elo);
				stmt.setString(2, p.getName());
				stmt.setString(3, g);
				stmt.setString(4, p.getUUID().toString());
				SQLManager.execute(statement);
				SQLManager.close(statement);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static CompletableFuture<Void> updateName(Profile p) {
		return CompletableFuture.runAsync(() -> {
			if (!PracticePlugin.DB_CONNECTED)
				return;
			try {
				if (!exists(p.getUUID().toString()))
					return;

				AutoCloseable[] statement = SQLManager
						.prepare("UPDATE " + TABLE + " SET PLAYER=? WHERE UUID=?");
				PreparedStatement stmt = (PreparedStatement) statement[0];
				stmt.setString(1, p.getName());
				stmt.setString(2, p.getUUID().toString());
				SQLManager.execute(statement);
				SQLManager.close(statement);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private static boolean exists(String uuid) throws Exception {
		if (!PracticePlugin.DB_CONNECTED)
			return false;
		AutoCloseable[] statement = SQLManager.prepare("SELECT * FROM " + TABLE + " WHERE UUID=?");
		PreparedStatement stmt = (PreparedStatement) statement[0];
		stmt.setString(1, uuid);
		AutoCloseable[] results = SQLManager.executeQuery(stmt);
		ResultSet r = (ResultSet) results[0];

		boolean returnVal = r.next();

		SQLManager.close(statement);
		SQLManager.close(results);

		return returnVal;
	}

	private static boolean exists(String uuid, String gametypeName) throws Exception {
		if (!PracticePlugin.DB_CONNECTED)
			return false;
		AutoCloseable[] statement = SQLManager.prepare("SELECT * FROM " + TABLE + " WHERE GAMETYPE=? AND UUID=?");
		PreparedStatement stmt = (PreparedStatement) statement[0];
		stmt.setString(1, gametypeName);
		stmt.setString(2, uuid);
		AutoCloseable[] results = SQLManager.executeQuery(stmt);
		ResultSet r = (ResultSet) results[0];

		boolean returnVal = r.next();

		SQLManager.close(statement);
		SQLManager.close(results);

		return returnVal;
	}

	public static CompletableFuture<Integer> get(Gametype gametype, UUID uuid) {
		return CompletableFuture.supplyAsync(() -> {
			if (!PracticePlugin.DB_CONNECTED)
				return 1000;
			try {
				AutoCloseable[] statement = SQLManager
						.prepare("SELECT * FROM " + TABLE + " WHERE GAMETYPE=? AND UUID=?");
				PreparedStatement stmt = (PreparedStatement) statement[0];
				stmt.setString(1, gametype.getName());
				stmt.setString(2, uuid.toString());
				AutoCloseable[] results = SQLManager.executeQuery(stmt);
				ResultSet r = (ResultSet) results[0];

				int returnVal = r.next() ? r.getInt("ELO") : 1000;

				SQLManager.close(results);
				SQLManager.close(statement);

				return returnVal;
			} catch (Exception e) {
				e.printStackTrace();
				return 1000;
			}
		});
	}

	public static CompletableFuture<Integer> get(Gametype gametype, String playerName) {
		return CompletableFuture.supplyAsync(() -> {
			if (!PracticePlugin.DB_CONNECTED)
				return 1000;
			try {
				AutoCloseable[] statement = SQLManager
						.prepare("SELECT * FROM " + TABLE + " WHERE PLAYER=? AND GAMETYPE=?");
				PreparedStatement stmt = (PreparedStatement) statement[0];
				stmt.setString(1, playerName);
				stmt.setString(2, gametype.getName());
				AutoCloseable[] results = SQLManager.executeQuery(stmt);
				ResultSet r = (ResultSet) results[0];

				int returnVal = r.next() ? r.getInt("ELO") : 1000;

				SQLManager.close(results);
				SQLManager.close(statement);

				return returnVal;
			} catch (Exception e) {
				e.printStackTrace();
				return 1000;
			}
		});
	}

	public static LeaderboardMap getLeaderboardMap(Gametype gametype) {
		try {
			LeaderboardMap map = new LeaderboardMap();

			if (!PracticePlugin.DB_CONNECTED)
				return map;

			AutoCloseable[] statement = SQLManager.prepare("SELECT * FROM " + TABLE + " WHERE GAMETYPE=?");

			if (statement == null)
				return map;

			PreparedStatement stmt = (PreparedStatement) statement[0];
			stmt.setString(1, gametype.getName());
			AutoCloseable[] results = SQLManager.executeQuery(stmt);
			ResultSet r = (ResultSet) results[0];

			while (r.next()) {

				map.put(r.getString("PLAYER"), r.getInt("ELO"));

			}

			SQLManager.close(statement);
			SQLManager.close(results);

			return map;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
