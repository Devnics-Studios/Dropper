package com.devnics.dropper;

import com.devnics.dropper.command.DropperCommand;
import com.devnics.dropper.listener.PlayerMoveListener;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DropperPlugin extends JavaPlugin {

    @Getter
    @Setter
    private static DropperPlugin instance;

    @Getter
    @Setter
    public Economy economy;

    @Getter
    @Setter
    public Game game;

    public BossBar bossBar = null;

    WorldGuardPlugin worldGuard;
    WorldGuard wg = WorldGuard.getInstance();
    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        setEconomy(rsp.getProvider());
        return getEconomy() != null;
    }
    @Override
    public void onEnable() {

        if (!setupEconomy()) {
            throw new RuntimeException("Please install vault");
        }

        worldGuard = (WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard");

        setInstance(this);
        this.bossBar = new BossBar();
        setGame(new Game());
        BukkitCommandManager<CommandSender> manager = BukkitCommandManager.create(this);

        manager.registerCommand(
                new DropperCommand()
        );

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(
                new PlayerMoveListener(),
                this
        );
    }

    @Override
    public void onDisable() {}

    public static String Color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
