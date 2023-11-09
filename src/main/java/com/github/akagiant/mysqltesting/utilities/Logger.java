package com.github.akagiant.mysqltesting.utilities;

import com.github.akagiant.mysqltesting.MySQLTesting;
import org.bukkit.Bukkit;

public class Logger {

	private Logger() {
		//no instance
	}

	public static void warn(String msg) {
		Bukkit.getConsoleSender().sendMessage(
			ColorManager.formatColours("&8[&6" + MySQLTesting.getPlugin().getName() + " &6&lWARN&8] " + msg)
		);
	}


	public static void severe(String msg) {
		Bukkit.getConsoleSender().sendMessage(
			ColorManager.formatColours("&8[&c" + MySQLTesting.getPlugin().getName() + " &c&lSEVERE&8] &f" + msg)
		);
	}

	public static void toConsole(String msg) {
		Bukkit.getConsoleSender().sendMessage(
			ColorManager.formatColours("&8[&b" + MySQLTesting.getPlugin().getName() + "&8] " + msg)
		);
	}
}
