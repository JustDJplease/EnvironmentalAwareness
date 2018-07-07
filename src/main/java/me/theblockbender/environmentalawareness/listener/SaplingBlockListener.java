package me.theblockbender.environmentalawareness.listener;

import me.theblockbender.environmentalawareness.Main;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class SaplingBlockListener implements Listener {
    private Main main;
    private List<Location> preventEasyFarm = new ArrayList<>();

    public SaplingBlockListener(Main main) {
        this.main = main;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void BlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (event.isCancelled()) return;
        if (block == null) return;
        if (block.getType() != Material.SAPLING) return;
        BlockState blockState = event.getBlockReplacedState();
        if (blockState.getType() != Material.AIR) return;
        Player player = event.getPlayer();
        EconomyResponse economyResponse = main.economy.depositPlayer(player, main.getConfig().getDouble("reward-money"));
        if (economyResponse.transactionSuccess()) {
            preventEasyFarm.add(block.getLocation());
            if (main.getConfig().getBoolean("should-reward-message"))
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("reward-message")));
        } else {
            main.getLogger().severe(" | Unable to give the sapling place reward to the player " + player.getName() + ".");
            main.getLogger().severe(" | An error occurred: " + economyResponse.errorMessage);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void BlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (event.isCancelled()) return;
        if (block == null) return;
        if (block.getType() != Material.SAPLING) return;
        preventEasyFarm.remove(block.getLocation());
        Player player = event.getPlayer();
        EconomyResponse economyResponse = main.economy.withdrawPlayer(player, main.getConfig().getDouble("penalty-money"));
        if (economyResponse.transactionSuccess()) {
            if (main.getConfig().getBoolean("should-penalty-message"))
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("penalty-message")));
        } else {
            main.getLogger().severe(" | Unable to take the sapling break penalty from the player " + player.getName() + ".");
            main.getLogger().severe(" | An error occurred: " + economyResponse.errorMessage);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void BlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (event.isCancelled()) return;
        if (block == null) return;
        if (block.getType() != Material.SAPLING) return;
        if (event.getChangedType() != Material.AIR) return;
        if (!preventEasyFarm.contains(block.getLocation())) return;
        preventEasyFarm.remove(block.getLocation());
        event.setCancelled(true);
        block.setType(Material.AIR);
    }
}
