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
    private int countdown = 60 * 10;
    private int initial = 60 * 10;
    private BossBar bossBar;

    public Timer() {
        this.bossBar = Bukkit.createBossBar(
                ChatColor.YELLOW + "Game has begun.",
                BarColor.GREEN,
                BarStyle.SOLID
        );

        for (UUID uuid: this.plugin.game.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) continue;

            this.bossBar.addPlayer(player);
        }
    }

    @Override
    public void run() {

        if (countdown == 0) {
            this.bossBar.removeAll();
            this.cancel();
            return;
        }

        if (countdown % 60 == 0) {
            this.bossBar.setProgress((initial / countdown) / 1000);
        }

        for (UUID uuid: this.plugin.game.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) continue;

            String a = ChatColor.YELLOW + Integer.toString(Math.round(this.countdown / 60)) + "m remaining";

            if (a == this.bossBar.getTitle()) continue;

            this.bossBar.setTitle(a);
        }
        countdown = countdown - 1;
    }
}
