package com.devnics.dropper;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class Game {

    private DropperPlugin plugin = DropperPlugin.getInstance();
    private ArrayList<UUID> players = new ArrayList<>();
    private String currentArena = null;
    private int currentRound = 0;

    public void addPlayer(Player player) {
        this.players.add(player.getUniqueId());

        preparePlayer(player);
    }

    public void preparePlayer(Player player) {
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
        player.setFoodLevel(20);
    }

    public void startGame(String arena) {
        this.currentArena = arena;
        ConfigurationSection section =  this.plugin.getConfig().getConfigurationSection("arenas." + arena);

        Location location = section.getLocation("spawn");

        for (UUID playerUUID: this.players) {
            Player player = Bukkit.getPlayer(playerUUID);

            if (player == null) {
                this.players.remove(playerUUID);
                continue;
            }

            player.teleport(location);
            preparePlayer(player);
            player.sendTitle(
                    ChatColor.GREEN + "" + ChatColor.BOLD + "GO!",
                    "",
                    10,
                    20 * 5,
                    20 * 5
            );

        }
    }

    public boolean isPlayer(Player player) {
        return this.players.contains(player.getUniqueId());
    }

    public ArrayList<UUID> getPlayers() {
        return this.players;
    }

    public void fail(Player player) {
        ConfigurationSection section =  this.plugin.getConfig().getConfigurationSection("arenas." + this.currentArena);

        Location location = section.getLocation("spawn");

        player.teleport(location);
    }

    public void checkObsticle(Block b, Player player) {
        Set<ProtectedRegion> regions = getRegions(b);

        if (regions == null) return;

        regions.forEach(r -> {
            if (r.getId().equalsIgnoreCase(this.currentArena)) {
                if (isStandingOnBlock(player)) {
                    fail(player);
                }
            }
        });
    }

    public boolean isStandingOnBlock(Entity e) {
        BoundingBox bb = e.getBoundingBox();
        Location min = new Location(e.getWorld(), bb.getMinX(), bb.getMinY()-.01, bb.getMinZ());
        return isStandingOnBlockNotAir(min.getBlock())
                || isStandingOnBlockNotAir(min.add(bb.getWidthX(), 0, 0).getBlock())
                || isStandingOnBlockNotAir(min.add(0, 0, bb.getWidthZ()).getBlock())
                || isStandingOnBlockNotAir(min.add(-bb.getWidthX(), 0, 0).getBlock());
    }

    public boolean isStandingOnBlockNotAir(Block block) {
        return !block.getType().isAir() && !block.getType().equals(Material.EMERALD_BLOCK);
    }

    public Set<ProtectedRegion> getRegions(final Block block) {
        final RegionManager rgm = this.plugin.wg.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(block.getWorld()));
        if (rgm == null) return null;

        BlockVector3 location = BlockVector3.at(block.getX(), block.getY(), block.getZ());

        final ApplicableRegionSet ars = rgm.getApplicableRegions(location);
        return ars.getRegions();
    }

    public void succeed(Player player) {
        ConfigurationSection section =  this.plugin.getConfig().getConfigurationSection("arenas." + this.currentArena);

        Location location = section.getLocation("spawn");

        player.teleport(location);

        player.sendTitle(
                ChatColor.GREEN + "" + ChatColor.BOLD + "You won " + ChatColor.YELLOW + Integer.toString(section.getInt("points")) + ChatColor.GREEN + " points!",
                "",
                10,
                20 * 5,
                20 * 5
        );

    }
}
