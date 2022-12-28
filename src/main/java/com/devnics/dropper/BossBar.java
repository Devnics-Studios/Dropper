package com.devnics.dropper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

public class BossBar {

    private org.bukkit.boss.BossBar bossBar = null;

    public BossBar() {
        this.bossBar = Bukkit.createBossBar(
                ChatColor.YELLOW + "Please wait..",
                BarColor.GREEN,
                BarStyle.SOLID
        );
    }

    public void addPlayer(Player player) {
        this.bossBar.addPlayer(player);
    }

    public void remove() {
        this.bossBar.removeAll();
        this.bossBar.setTitle(ChatColor.YELLOW + "Please wait..");
    }

    public void setTitle(String title) {
        this.bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', title));
    }

    public void setProgress(double i) {
        this.bossBar.setProgress(i);
    }
}
