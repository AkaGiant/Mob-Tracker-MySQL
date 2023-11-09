package com.github.akagiant.mysqltesting.listeners;

import com.github.akagiant.mysqltesting.sqlutils.SQLUtil;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class OnMobKill implements Listener {

	@EventHandler
	public void onMobKill(EntityDeathEvent e) {
		if (e.getEntity().getKiller() == null) return;
		SQLUtil.updateMobKills(e.getEntity().getKiller(), (Mob) e.getEntity());
	}

}
