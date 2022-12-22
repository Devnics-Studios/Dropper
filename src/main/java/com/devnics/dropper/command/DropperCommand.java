package com.devnics.dropper.command;

import com.devnics.dropper.DropperPlugin;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Requirement;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.message.MessageKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        this.plugin.getConfig().set("arenas." + name + ".points", 20);
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

    @SubCommand("start")
    public void StartGame(CommandSender sender, String arena) {
        this.plugin.getGame().startGame(arena);
    }

}

