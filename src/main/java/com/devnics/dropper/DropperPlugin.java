package com.devnics.dropper;

import com.devnics.dropper.command.DropperCommand;
import com.devnics.dropper.listener.PlayerMoveListener;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class DropperPlugin extends JavaPlugin {

    @Getter
    @Setter
    private static DropperPlugin instance;

    @Getter
    @Setter
    public Game game;

    WorldGuardPlugin worldGuard;
    WorldGuard wg = WorldGuard.getInstance();

    @Override
    public void onEnable() {
        worldGuard = (WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard");

        setInstance(this);
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


}
