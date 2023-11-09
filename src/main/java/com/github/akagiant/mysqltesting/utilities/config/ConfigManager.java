package com.github.akagiant.mysqltesting.utilities.config;

public class ConfigManager {

	private ConfigManager() {
		//no instance
	}

	public static Config config, messages;

	public static void registerConfigurations() {
		config = new Config("config");
		messages = new Config("messages");
	}

}
