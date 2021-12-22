package ms.uk.eclipse.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.sql.SQLManager;
import ms.uk.eclipse.entity.Profile;

public class EloManager {
	public String table = "Elo";
	GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();

	public EloManager() {
		try {
			AutoCloseable[] stmt = SQLManager.prepare("CREATE TABLE IF NOT EXISTS " + table
					+ " (GAMETYPE VARCHAR(200),UUID VARCHAR(200),PLAYER VARCHAR(200),ELO INT NOT NULL)");
			SQLManager.execute(stmt);
			SQLManager.close(stmt);
		} catch (Exception e) {
			System.out.println("FAILED TO CONNECT TO DATABASE");
		}
	}

	public void updateElo(Profile p, String g, int elo) {
		try {
			if (eloEntryExists(p.getUUID().toString(), g)) {
				AutoCloseable[] statement = SQLManager
						.prepare("UPDATE " + table + " SET ELO=? WHERE GAMETYPE=? AND PLAYER=? AND UUID=?");
				PreparedStatement stmt = (PreparedStatement) statement[0];
				stmt.setInt(1, elo);
				stmt.setString(2, g);
				stmt.setString(3, p.getName());
				stmt.setString(4, p.getUUID().toString());
				SQLManager.execute(statement);
				SQLManager.close(statement);
				return;
			}

			setEloEntry(p, g, elo);
		} catch (Exception e) {
			System.out.println("FAILED TO CONNECT TO DATABASE");
		}
	}

	public boolean eloEntryExists(String uuid, String g) {
		try {
			AutoCloseable[] statement = SQLManager.prepare("SELECT * FROM " + table + " WHERE GAMETYPE=? AND UUID=?");
			PreparedStatement stmt = (PreparedStatement) statement[0];
			stmt.setString(1, g);
			stmt.setString(2, uuid);
			AutoCloseable[] results = SQLManager.executeQuery(stmt);
			ResultSet r = (ResultSet) results[0];

			boolean returnVal = r.next();

			SQLManager.close(statement);
			SQLManager.close(results);

			return returnVal;

		} catch (Exception e) {
			System.out.println("FAILED TO CONNECT TO DATABASE");
		}
		return false;
	}

	public void setEloEntry(final Profile p, final String g, int i) {
		try {
			AutoCloseable[] insert = SQLManager
					.prepare("INSERT INTO " + table + " (GAMETYPE, UUID, PLAYER, ELO) VALUES (?, ?, ?, ?)");
			PreparedStatement stmt = (PreparedStatement) insert[0];
			stmt.setString(1, g);
			stmt.setString(2, p.getUUID().toString());
			stmt.setString(3, p.getName());
			stmt.setInt(4, i);
			SQLManager.execute(insert);
			SQLManager.close(insert);
		} catch (Exception e) {
			System.out.println("FAILED TO CONNECT TO DATABASE");
		}
	}

	public int getEloEntry(String g, UUID uuid) {

		try {
			AutoCloseable[] statement = SQLManager.prepare("SELECT * FROM " + table + " WHERE GAMETYPE=? AND UUID=?");
			PreparedStatement stmt = (PreparedStatement) statement[0];
			stmt.setString(1, g);
			stmt.setString(2, uuid.toString());
			AutoCloseable[] results = SQLManager.executeQuery(stmt);
			ResultSet r = (ResultSet) results[0];

			int returnVal = r.next() ? r.getInt("ELO") : 1000;

			SQLManager.close(results);
			SQLManager.close(statement);

			return returnVal;
		} catch (Exception e) {
			System.out.println("FAILED TO CONNECT TO DATABASE");
		}
		return 1000;
	}

	public int getEloEntry(String g, String name) {

		try {
			AutoCloseable[] statement = SQLManager.prepare("SELECT * FROM " + table + " WHERE GAMETYPE=? AND PLAYER=?");
			PreparedStatement stmt = (PreparedStatement) statement[0];
			stmt.setString(1, g);
			stmt.setString(2, name);
			AutoCloseable[] results = SQLManager.executeQuery(stmt);
			ResultSet r = (ResultSet) results[0];

			int returnVal = r.next() ? r.getInt("ELO") : 1000;

			SQLManager.close(results);
			SQLManager.close(statement);

			return returnVal;
		} catch (Exception e) {
			System.out.println("FAILED TO CONNECT TO DATABASE");
		}
		return 1000;
	}
}
