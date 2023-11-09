package com.github.akagiant.mysqltesting;

import com.github.akagiant.mysqltesting.listeners.OnMobKill;
import com.github.akagiant.mysqltesting.sqlutils.SQLUtil;
import com.github.akagiant.mysqltesting.utilities.Logger;
import com.github.akagiant.mysqltesting.utilities.config.ConfigManager;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.sql.SQLException;

public final class MySQLTesting extends JavaPlugin implements Listener {

	@Getter
	private static Plugin plugin;

	public static DataSource dataSource;
	private static boolean enableDevMode = false;

    @Override
    public void onEnable() {
		// Plugin startup logic
		plugin = this;

		ConfigManager.registerConfigurations();

		getServer().getPluginManager().registerEvents(new OnMobKill(), this);

		try {
			dataSource = SQLUtil.initMySQLDataSource();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		SQLUtil.setupDB();
	}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
		try {
			dataSource.getConnection().close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static void devLog(String toLog) {
		if (enableDevMode) {
			Logger.severe(toLog);
		}
	}
}
