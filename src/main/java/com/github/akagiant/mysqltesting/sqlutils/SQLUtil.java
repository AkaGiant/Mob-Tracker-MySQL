package com.github.akagiant.mysqltesting.sqlutils;

import com.github.akagiant.mysqltesting.MySQLTesting;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import javax.sql.DataSource;
import java.sql.*;
import java.util.UUID;

public class SQLUtil {

	private SQLUtil() {
		//no instance
	}
	
	public static DataSource initMySQLDataSource() throws SQLException {
		MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
		// set credentials
		dataSource.setServerName("");
		dataSource.setPort();
		dataSource.setDatabaseName("");
		dataSource.setUser("");
		dataSource.setPassword("");

		// Test connection
		testDataSource(dataSource);
		return dataSource;
	}

	public static  void testDataSource(DataSource dataSource) throws SQLException {
		try (Connection conn = dataSource.getConnection()) {
			if (!conn.isValid(1)) {
				throw new SQLException("Could not establish database connection.");
			}
		}
	}

	public static  void setupDB() {
		try(Connection conn = MySQLTesting.dataSource.getConnection();
			Statement stmt = conn.createStatement();
		) {
			String sql = "CREATE TABLE IF NOT EXISTS mob_kills " +
				"(uuid VARCHAR(255) not NULL, " +
				"PRIMARY KEY ( uuid ))";

			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			Bukkit.getLogger().severe("Unable to create base Tables in the Database.");
			e.printStackTrace();
		}
	}


	public static  boolean createPlayer(Player player) {
		if (!tableExists()) setupDB();

		if (playerExists(player.getUniqueId())) return true;

		try (Connection conn = MySQLTesting.dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(
			"INSERT INTO mob_kills (uuid) VALUES (?)")) {
			stmt.setString(1, player.getUniqueId().toString());
			stmt.execute();

			MySQLTesting.devLog("Player Created");

			return true;
		} catch (SQLException e) {
			Bukkit.getLogger().severe("Could not create player");
			e.printStackTrace();
		}
		return false;
	}

	public static  boolean playerExists(UUID uuid) {
		try (Connection conn = MySQLTesting.dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(
			"SELECT * FROM mob_kills WHERE uuid = ?")) {
			stmt.setString(1, uuid.toString());
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				MySQLTesting.devLog("Player Exists");
				return true;
			}
			MySQLTesting.devLog("Player Does Not Exist");
			return false;

		} catch (SQLException e) {
			Bukkit.getLogger().severe("Unable to check if Player Exists");
			e.printStackTrace();
		}
		return false;
	}

	public static  boolean updateMobKills(Player player, Mob mob) {
		// Won't attempt to update a players kills if there was an error creating the user.
		boolean createPlayer = createPlayer(player);
		if (!createPlayer) return false;

		// Ensure the table has a mob column for the killed mob.
		boolean tableHasMob = tableHasMobColumn(mob);
		if (!tableHasMob) {
			createMobColumn(mob);
		}

		try (Connection conn = MySQLTesting.dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(
			"INSERT INTO mob_kills (uuid," + mob.getName().toLowerCase() + ") VALUES(?,1) ON DUPLICATE KEY UPDATE " + mob.getName().toLowerCase() + " = " + mob.getName().toLowerCase() + " + 1")) {
			stmt.setString(1, player.getUniqueId().toString());
			stmt.execute();

			MySQLTesting.devLog("Kills Updated for " + player.getName() + " tracking " + mob.getName() + "(s)");

			return true;
		} catch (SQLException e) {
			Bukkit.getLogger().severe("Failed to Updated Kills for " + player.getName() + " tracking " + mob.getName() + "(s)");
			e.printStackTrace();
		}
		return false;
	}

	public static  boolean createMobColumn(Mob mob) {
		try (Connection conn = MySQLTesting.dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(
			"ALTER TABLE mob_kills ADD COLUMN "+ mob.getName().toLowerCase() + " INT NOT NULL")) {
			stmt.execute();
			MySQLTesting.devLog("New Column for " + mob.getName() + " Created");
			return true;
		} catch (SQLException e) {
			Bukkit.getLogger().severe("Unable to create new column in mob_kills for mob " + mob.getName());
			e.printStackTrace();
		}
		return false;
	}

	public static  boolean tableExists() {
		try (Connection conn = MySQLTesting.dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(
			"SELECT * FROM information_schema.COLUMNS " +
				"WHERE " +
				"TABLE_SCHEMA = 'Minecraft' " +
				"AND TABLE_NAME = 'mob_kills'")) {

			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				MySQLTesting.devLog("mob_kills table already exists.");
				return true;
			}
			MySQLTesting.devLog("mob_kills table does not exist.");
			return false;
		} catch (SQLException e) {
			Bukkit.getLogger().severe("Unable to determine if the mob_kills table exists.");
			e.printStackTrace();
		}
		return false;
	}

	public static  boolean tableHasMobColumn(Mob mob) {
		try (Connection conn = MySQLTesting.dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(
			"SELECT * FROM information_schema.COLUMNS " +
				"WHERE " +
				"TABLE_SCHEMA = 'Minecraft' " +
				"AND TABLE_NAME = 'mob_kills' " +
				"AND COLUMN_NAME = ?")) {
			stmt.setString(1, mob.getName().toLowerCase());

			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				MySQLTesting.devLog("Mob Column for Mob " + mob.getName() + " Already Exists.");
				return true;
			}
			MySQLTesting.devLog("Mob Column for Mob " + mob.getName() + " Does Not Exists.");
			return false;
		} catch (SQLException e) {
			Bukkit.getLogger().severe("Unable to determine if a column for mob " + mob.getName() + " exists.");
			e.printStackTrace();
		}
		return false;
	}

}
