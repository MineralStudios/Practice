package gg.mineral.practice.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import gg.mineral.core.sql.SQLManager;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.util.LeaderboardMap;

public class EloManager {
	public static String TABLE = "Elo";

	static {
		try {
			AutoCloseable[] stmt = SQLManager.prepare("CREATE TABLE IF NOT EXISTS " + TABLE
					+ " (ELO INT NOT NULL, PLAYER_NAME VARCHAR(200), GAMETYPE_NAME VARCHAR(200), UUID_MOST_SIGNIFICANT_BITS BIGINT NOT NULL, UUID_LEAST_SIGNIFICANT_BITS BIGINT NOT NULL)");
			SQLManager.execute(stmt);
			SQLManager.close(stmt);
		} catch (Exception e) {
			System.out.println("FAILED TO CONNECT TO DATABASE");
		}
	}

	public static void update(Profile profile, Gametype gametype, int elo) throws SQLException {
		AutoCloseable[] statement = SQLManager
				.prepare(exists(profile, gametype) ? "UPDATE " + TABLE
						+ " SET ELO=?, PLAYER_NAME=? WHERE GAMETYPE_NAME=? AND UUID_MOST_SIGNIFICANT_BITS=? AND UUID_LEAST_SIGNIFICANT_BITS=?"
						: "INSERT INTO " + TABLE
								+ " (ELO, PLAYER_NAME, GAMETYPE_NAME, UUID_MOST_SIGNIFICANT_BITS, UUID_LEAST_SIGNIFICANT_BITS) VALUES (?, ?, ?, ?, ?)");

		PreparedStatement stmt = (PreparedStatement) statement[0];
		stmt.setInt(1, elo);
		stmt.setString(2, profile.getName());
		stmt.setString(3, gametype.getName());
		stmt.setLong(4, profile.getUUID().getMostSignificantBits());
		stmt.setLong(5, profile.getUUID().getLeastSignificantBits());
		SQLManager.execute(statement);
		SQLManager.close(statement);
	}

	private static boolean exists(Profile profile, Gametype gametype) throws SQLException {
		AutoCloseable[] statement = SQLManager.prepare("SELECT * FROM " + TABLE
				+ " WHERE GAMETYPE_NAME=? AND UUID_MOST_SIGNIFICANT_BITS=? AND UUID_LEAST_SIGNIFICANT_BITS=?");
		PreparedStatement stmt = (PreparedStatement) statement[0];
		stmt.setString(1, gametype.getName());
		stmt.setLong(2, profile.getUUID().getMostSignificantBits());
		stmt.setLong(3, profile.getUUID().getLeastSignificantBits());
		AutoCloseable[] results = SQLManager.executeQuery(stmt);
		ResultSet r = (ResultSet) results[0];

		boolean returnVal = r.next();

		SQLManager.close(statement);
		SQLManager.close(results);

		return returnVal;
	}

	public static int get(UUID uuid, Gametype gametype) throws SQLException {
		AutoCloseable[] statement = SQLManager.prepare("SELECT * FROM " + TABLE
				+ " WHERE GAMETYPE_NAME=? AND UUID_MOST_SIGNIFICANT_BITS=? AND UUID_LEAST_SIGNIFICANT_BITS=?");
		PreparedStatement stmt = (PreparedStatement) statement[0];
		stmt.setString(1, gametype.getName());
		stmt.setLong(2, uuid.getMostSignificantBits());
		stmt.setLong(3, uuid.getLeastSignificantBits());
		AutoCloseable[] results = SQLManager.executeQuery(stmt);
		ResultSet r = (ResultSet) results[0];

		int returnVal = r.next() ? r.getInt("ELO") : 1000;

		SQLManager.close(results);
		SQLManager.close(statement);

		return returnVal;
	}

	public static int getByName(String name, Gametype gametype) throws SQLException {
		AutoCloseable[] statement = SQLManager.prepare("SELECT * FROM " + TABLE
				+ " WHERE PLAYER_NAME=? AND GAMETYPE_NAME=?");
		PreparedStatement stmt = (PreparedStatement) statement[0];
		stmt.setString(1, name);
		stmt.setString(2, gametype.getName());
		AutoCloseable[] results = SQLManager.executeQuery(stmt);
		ResultSet r = (ResultSet) results[0];

		int returnVal = r.next() ? r.getInt("ELO") : 1000;

		SQLManager.close(results);
		SQLManager.close(statement);

		return returnVal;
	}

	public static LeaderboardMap getLeaderboardMap(Gametype gametype) throws SQLException {
		LeaderboardMap map = new LeaderboardMap();

		AutoCloseable[] statement = SQLManager.prepare("SELECT * FROM " + TABLE + " WHERE GAMETYPE_NAME=?");
		PreparedStatement stmt = (PreparedStatement) statement[0];
		stmt.setString(1, gametype.getName());
		AutoCloseable[] results = SQLManager.executeQuery(stmt);
		ResultSet r = (ResultSet) results[0];

		while (r.next()) {
			map.put(r.getString("PLAYER_NAME"), r.getInt("ELO"), false);
		}

		SQLManager.close(statement);
		SQLManager.close(results);

		return map;
	}
}
