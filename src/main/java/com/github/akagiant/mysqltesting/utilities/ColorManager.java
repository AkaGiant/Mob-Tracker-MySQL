package com.github.akagiant.mysqltesting.utilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorManager {

	private ColorManager() {
		//no instance
	}

	public static String formatColours(String msg) {
		Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
		String[] bukkitVer = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
		double converted = Double.parseDouble(bukkitVer[0] + "." + bukkitVer[1]);
		if (converted >= 1.16D) {
			for(Matcher match = pattern.matcher(msg); match.find(); match = pattern.matcher(msg)) {
				String color = msg.substring(match.start(), match.end());
				msg = msg.replace(color, ChatColor.of(color) + "");
			}
		}

		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static List<String> formatColours(List<String> unformatted) {
		List<String> formatted = new ArrayList<>();
		for (String str : unformatted) {
			formatted.add(formatColours(str));
		}
		return formatted;
	}

}


