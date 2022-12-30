package com.devnics.dropper.command;

import com.devnics.dropper.DropperPlugin;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

@Command("dropper")
public class DropperCommand extends BaseCommand {

    private DropperPlugin plugin = DropperPlugin.getInstance();
    @SubCommand("createarena")
    @Permission("dropper.admin")
    public void Arena(Player player, String name) {

        this.plugin.getConfig().set("arenas." + name + ".spawn", "");
        this.plugin.getConfig().set("arenas." + name + ".wall", null);
        this.plugin.getConfig().set("arenas." + name + ".emerald-points", 200);
        this.plugin.getConfig().set("arenas." + name + ".diamond-points", 150);
        this.plugin.getConfig().set("arenas." + name + ".iron-points", 100);
        this.plugin.saveConfig();

        player.sendMessage(
                DropperPlugin.Color("&8[&6Devnics&8] &aCreated arena &d{}")
                        .replace("{}", name)
        );
    }

    @SubCommand("setarenaspawn")
    @Permission("dropper.admin")
    public void SetArenaSpawn(Player player, String name) {

        this.plugin.getConfig().set("arenas." + name + ".spawn", player.getLocation());
        this.plugin.saveConfig();

        player.sendMessage(
                DropperPlugin.Color("&8[&6Devnics&8] &aSet the arena spawn for &d{}")
                        .replace("{}", name)
        );
    }

    @SubCommand("setpoints")
    @Permission("dropper.admin")
    public void SetPoints(CommandSender sender, String arena, Integer EmeraldPoints, Integer DiamondPoints, Integer IronPoints) {
        this.plugin.getConfig().set("arenas." + arena + ".emerald-points", EmeraldPoints);
        this.plugin.getConfig().set("arenas." + arena + ".diamond-points", DiamondPoints);
        this.plugin.getConfig().set("arenas." + arena + ".iron-points", IronPoints);
        this.plugin.saveConfig();

        sender.sendMessage(
                DropperPlugin.Color("&8[&6Devnics&8] &aSet the points for &d{arena} &a->").replace("{arena}", arena),
                DropperPlugin.Color("&a&lEmerald &7-> &e" + EmeraldPoints),
                DropperPlugin.Color("&b&Diamond &7-> &e" + DiamondPoints),
                DropperPlugin.Color("&f&lEmerald &7-> &e" + IronPoints)
        );
    }

    @SubCommand("reset")
    public void ResetPlayer(Player player) {
        this.plugin.getGame().reset(player);
        this.plugin.getGame().teleportToSpawn(player);
    }
    @SubCommand("join")
    public void JoinGame(Player player) {
        String worldName = this.plugin.getConfig().getString("waitinglobby");
        World world = Bukkit.getWorld(worldName);

        this.plugin.getGame().addPlayer(player);
        this.plugin.bossBar.addPlayer(player);

        player.teleport(world.getSpawnLocation());

        player.sendMessage(
                DropperPlugin.Color("&8[&6Devnics&8] &7You are now in the waiting lobby for &bDropper")
        );
    }

    @SubCommand("setwallmaterial")
    @Permission("dropper.admin")
    public void SetWallMaterial(Player player, String arena, Material material) {
        this.plugin.getConfig().set("arenas." + arena + ".wall", material.name());
        this.plugin.saveConfig();

        player.sendMessage(
                DropperPlugin.Color("&8[&6Devnics&8] &aSet wall material to &d{} &afor &d{arena}")
                        .replace("{}", material.name())
                        .replace("{arena}", arena)
        );
    }
    @SubCommand("start")
    @Permission("dropper.admin")
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
    @SubCommand("setwinnerarea")
    @Permission("dropper.admin")
    public void setwinnerarea(Player player) {
        this.plugin.getConfig().set("arena-wait", player.getLocation());
        this.plugin.saveConfig();

        player.sendMessage(
                DropperPlugin.Color("&8[&6Devnics&8] &aSet the area for winners.")
        );
    }

    @SubCommand("next")
    @Permission("dropper.admin")
    public void next(Player player) {
        this.plugin.getGame().nextStage();
        player.sendMessage(
                DropperPlugin.Color("&8[&6Devnics&8] &cMoving to next stage..")
        );
    }
}

