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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Game {

    private DropperPlugin plugin = DropperPlugin.getInstance();
    private ArrayList<UUID> players = new ArrayList<>();

    // Used for storing initial Vault Money Values.
    private HashMap<UUID, Integer> scores = new HashMap<>();
    private String currentArena = null;
    private BukkitRunnable timer = null;

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

    public boolean inRegion(Block b) {
        Set<ProtectedRegion> regions = getRegions(b);

        if (regions == null) return false;

        boolean toReturn = false;

        for (ProtectedRegion region: regions) {
            if (region.getId().equalsIgnoreCase(this.currentArena)) {
                toReturn = true;
                break;
            } else continue;
        }

        return toReturn;

    }

    public List<UUID> getPlayers() {
        return this.players;
    }
    public void startGame(String arena) {
        this.currentArena = arena;
        this.timer = new Timer();

        this.timer.runTaskTimer(this.plugin, 0, 20 * 60);

        ConfigurationSection section =  this.plugin.getConfig().getConfigurationSection("arenas." + arena);

        for (UUID playerUUID: this.players) {
            Player player = Bukkit.getPlayer(playerUUID);

            if (player == null) {
                this.players.remove(playerUUID);
                continue;
            }

            teleportToSpawn(player);
            preparePlayer(player);
            player.sendMessage(
                    "",
                    DropperPlugin.Color("&6&lDropper Minigame"),
                    "",
                    DropperPlugin.Color("&fThere are 3 possible goals you can land on."),
                    DropperPlugin.Color("&2&lEmerald &7-> &e" + section.getInt("emerald-points")),
                    DropperPlugin.Color("&b&lDiamond &7-> &e" + section.getInt("diamond-points")),
                    DropperPlugin.Color("&f&lIron &7-> &e" + section.getInt("iron-points")),
                    "",
                    DropperPlugin.Color("&dYou can keep trying after winning, but your coins will reset!")
            );

        }
    }

    public boolean isPlayer(Player player) {
        return this.players.contains(player.getUniqueId());
    }

    public void nextStage() {
        String arena = this.getNextArena();

        if (arena.equals("done")) {
            for (UUID plu: this.getPlayers()) {
                Player player = Bukkit.getPlayer(plu);

                if (player != null) {
                    Bukkit.dispatchCommand(player, "spawn");
                    player.sendMessage(
                            "",
                            DropperPlugin.Color("&c&lGame Ended"),
                            "",
                            DropperPlugin.Color("&fThank you for participating."),
                            DropperPlugin.Color("&6Devnics &fwishes you a pleasent day!"),
                            ""
                    );
                    this.plugin.economy.depositPlayer(player, 20);
                    player.sendTitle(
                            ChatColor.GREEN + "Participation Reward",
                            ChatColor.YELLOW + "$20",
                            10,
                            20 * 3,
                            10
                    );
                }
            }
            this.players = new ArrayList<>();
            this.currentArena = null;
            this.scores = new HashMap<>();
            this.timer = null;
            return;
        }

        startGame(arena);
    }
    public void fail(Player player) {
        teleportToSpawn(player);
    }

    public void succeed(Player player, Block block) {
        Location location = this.plugin.getConfig().getLocation("arena-wait");

        player.teleport(location);

        int points = this.plugin.getConfig().getInt("arenas." + this.currentArena + ".emerald-points");

        if (block.getType().equals(Material.DIAMOND_BLOCK)) {
            points = this.plugin.getConfig().getInt("arenas." + this.currentArena + ".diamond-points");
        }

        if (block.getType().equals(Material.IRON_BLOCK)) {
            points = this.plugin.getConfig().getInt("arenas." + this.currentArena + ".iron-points");
        }

        this.plugin.economy.depositPlayer(player.getName(), points);
        this.scores.put(player.getUniqueId(), points);

        Bukkit.broadcastMessage(
                DropperPlugin.Color("&e{} &7completed the dropper and won &d{c} &7coins!")
                        .replace("{}", player.getName())
                        .replace("{c}", Integer.toString(points))
        );

        player.sendTitle(
                ChatColor.YELLOW + "+$" + Integer.toString(points),
                "",
                10,
                20 * 5,
                20 * 5
        );

    }

    public void reset(Player player) {
        if (this.scores.get(player.getUniqueId()) != null) {
            this.plugin.economy.withdrawPlayer(player, this.scores.get(player.getUniqueId()));
        }
        this.scores.put(player.getUniqueId(), 0);
    }

    public void teleportToSpawn(Player player) {
        ConfigurationSection section =  this.plugin.getConfig().getConfigurationSection("arenas." + this.currentArena);

        Location location = section.getLocation("spawn");

        player.teleport(location);
    }
    public String getNextArena() {

        String nextArena = "done";
        boolean next = false;

        for (String key: this.plugin.getConfig().getConfigurationSection("arenas").getKeys(false)) {
            if (next) {
                nextArena = key;
                break;
            }
            if (key.equalsIgnoreCase(this.currentArena)) {
                next = true;
            }
        }
        return nextArena;
    }

    public void checkObsticle(Block b, Player player) {

        if (!inRegion(b)) return;
        if (!isStandingOnBlock(player)) return;

        fail(player);
        return;
    }

    public boolean isStandingOnBlock(Entity e) {
        Block block1 = e.getLocation().subtract(e.getWidth() / 2, 1, e.getWidth() / 2).getBlock();
        Block block2 = e.getLocation().subtract(-e.getWidth() / 2, 1, e.getWidth() / 2).getBlock();
        Block block3 = e.getLocation().subtract(e.getWidth() / 2, 1, -e.getWidth() / 2).getBlock();
        Block block4 = e.getLocation().subtract(-e.getWidth() / 2, 1, -e.getWidth() / 2).getBlock();

        return isValid(block1) || isValid(block2) || isValid(block3) || isValid(block4);
    }

    public boolean isValid(Block block) {
        return !block.getType().isAir() &&
                !block.getType().equals(Material.EMERALD_BLOCK) &&
                !block.getType().equals(
                        Material.valueOf(
                                this.plugin.getConfig().getString("arenas." + this.currentArena + ".wall")
                        )
                );
    }

    public Set<ProtectedRegion> getRegions(final Block block) {
        final RegionManager rgm = this.plugin.wg.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(block.getWorld()));
        if (rgm == null) return null;

        BlockVector3 location = BlockVector3.at(block.getX(), block.getY(), block.getZ());

        final ApplicableRegionSet ars = rgm.getApplicableRegions(location);
        return ars.getRegions();
    }
}
