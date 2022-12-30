package com.devnics.dropper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Timer extends BukkitRunnable {

    private DropperPlugin plugin = DropperPlugin.getInstance();
    private int countdown = 10;

    @Override
    public void run() {

        if (countdown == 0) {
            this.plugin.bossBar.remove();
            this.plugin.getGame().nextStage();
            this.cancel();
            return;
        }

        for (UUID uuid: this.plugin.game.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) continue;

            String a = ChatColor.YELLOW + Integer.toString(this.countdown) + "m remaining";

            this.plugin.bossBar.setTitle(a);

            if (countdown <= 5 && countdown % 2 != 0) {
                player.sendMessage(
                        DropperPlugin.Color("&cGame ending in &b{} &cminutes!")
                                .replace("{}", Integer.toString(countdown))
                );
            }
        }
        countdown = countdown - 1;
    }
}
