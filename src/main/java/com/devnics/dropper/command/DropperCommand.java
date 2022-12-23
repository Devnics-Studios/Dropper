package com.devnics.dropper.command;

import com.devnics.dropper.DropperPlugin;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Requirement;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.message.MessageKey;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

@Command("dropper")
@Permission("dropper.admin")
public class DropperCommand extends BaseCommand {

    private DropperPlugin plugin = DropperPlugin.getInstance();

    @Default
    public void executor(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "/dropper <arena> [..]");
    }

    @SubCommand("createarena")
    public void Arena(Player player, String name) {

        this.plugin.getConfig().set("arenas." + name + ".spawn", "");
        this.plugin.getConfig().set("arenas." + name + ".wall", null);
        this.plugin.saveConfig();

        player.sendMessage(
                ChatColor.GREEN + "Created arena {}, please use WorldGuard and create a region named {}"
                        .replace("{}", ChatColor.YELLOW + name + ChatColor.GREEN)
        );
    }

    @SubCommand("setarenaspawn")
    public void SetArenaSpawn(Player player, String name) {

        this.plugin.getConfig().set("arenas." + name + ".spawn", player.getLocation());
        this.plugin.saveConfig();

        player.sendMessage(
                ChatColor.GREEN + "Set arena spawn for {} at your current co-ordinates."
                        .replace("{}", ChatColor.YELLOW + name + ChatColor.GREEN)
        );
    }

    @SubCommand("join")
    public void JoinGame(CommandSender sender, Player target) {

        String worldName = this.plugin.getConfig().getString("waitinglobby");
        World world = Bukkit.getWorld(worldName);

        this.plugin.getGame().addPlayer(target);

        target.teleport(world.getSpawnLocation());
    }

    @SubCommand("setwallmaterial")
    public void SetWallMaterial(Player player, String arena, Material material) {
        this.plugin.getConfig().set("arenas." + arena + ".wall", material.name());
        this.plugin.saveConfig();

        player.sendMessage(
                ChatColor.GREEN + "Set arena material for {1} to {2}"
                        .replace("{1}", ChatColor.YELLOW + arena + ChatColor.GREEN)
                        .replace("{2}", ChatColor.YELLOW + material.name() + ChatColor.GREEN)
        );
    }
    @SubCommand("start")
    public void StartGame(CommandSender sender) {
        Set<String> keys = this.plugin
                .getConfig()
                .getConfigurationSection("arenas")
                .getKeys(false);

        assert keys.isEmpty() == false;

        this.plugin.getGame().startGame(
               keys.iterator().next()
        );
    }

    @SubCommand("test")
    public void test(CommandSender sender) {
        sender.sendMessage(
                this.plugin.getGame().getNextArena()
        );
    }

    @SubCommand("setarenawait")
    public void SetWaitArena(Player player) {
        this.plugin.getConfig().set("arena-wait", player.getLocation());
        this.plugin.saveConfig();

        player.sendMessage(
                ChatColor.GREEN + "Set arena waiting area location"
        );
    }
}

