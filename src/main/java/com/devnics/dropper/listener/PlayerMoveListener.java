package com.devnics.dropper.listener;

import com.devnics.dropper.DropperPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    DropperPlugin plugin = DropperPlugin.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!this.plugin.getGame().isPlayer(player)) return;

        Block block = event.getTo().clone().subtract(0, 1, 0).getBlock();

        if ((block.getType().equals(Material.EMERALD_BLOCK) || block.getType().equals(Material.DIAMOND_BLOCK) || block.getType().equals(Material.IRON_BLOCK)) && this.plugin.getGame().inRegion(block)) {
            this.plugin.getGame().succeed(player, block);
        } else {
            this.plugin.getGame().checkObsticle(block, player);
        }
    }
}
