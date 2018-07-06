package me.theblockbender.environmentalawareness.listener;

import me.theblockbender.environmentalawareness.Main;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class SaplingBlockListener implements Listener {
    private Main main;

    public SaplingBlockListener(Main main) {
        this.main = main;
    }

    @EventHandler(ignoreCancelled = true)
    public void BlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block == null) return;
        if (block.getType() != Material.SAPLING) return;
        Player player = event.getPlayer();
        EconomyResponse economyResponse = main.economy.depositPlayer(player, main.getConfig().getDouble("reward-money"));
        if (economyResponse.transactionSuccess()) {
            if (main.getConfig().getBoolean("should-reward-message"))
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("reward-message")));
        } else {
            main.getLogger().severe(" | Unable to give the sapling place reward to the player " + player.getName() + ".");
            main.getLogger().severe(" | An error occurred: " + economyResponse.errorMessage);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void BlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block == null) return;
        if (block.getType() != Material.SAPLING) return;
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
}
