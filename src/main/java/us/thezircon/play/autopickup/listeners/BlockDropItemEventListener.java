package us.thezircon.play.autopickup.listeners;

import com.sun.tools.javac.jvm.Items;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.utils.AutoSmelt;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BlockDropItemEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrop(BlockDropItemEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        boolean doFullInvMSG = PLUGIN.getConfig().getBoolean("doFullInvMSG");
        boolean doBlacklist = PLUGIN.getBlacklistConf().getBoolean("doBlacklisted");
        boolean voidOnFullInv = false;
        boolean doSmelt = PLUGIN.auto_smelt_blocks.contains(player);

        if (PLUGIN.getConfig().contains("voidOnFullInv")) {
            voidOnFullInv = PLUGIN.getConfig().getBoolean("voidOnFullInv");
        }

        List<String> blacklist = PLUGIN.getBlacklistConf().getStringList("Blacklisted");

        Location loc = block.getLocation();
        if (AutoPickup.worldsBlacklist!=null && AutoPickup.worldsBlacklist.contains(loc.getWorld().getName())) {
            return;
        }

        if (block.getState() instanceof Container) {
            return; // Containers are handled in block break event
        }

        if (PLUGIN.autopickup_list.contains(player)) { // Player has auto enabled
            HashMap<Integer, ItemStack> leftOver = new HashMap<Integer, ItemStack>();


            for (Entity en : e.getItems()) {

                Item i = (Item) en;
                ItemStack drop = i.getItemStack();
                int amount = drop.getAmount();
                player.getInventory().removeItem(new ItemStack(i.getItemStack().getType(), 1));
                leftOver.putAll((player.getInventory().addItem(new ItemStack(i.getItemStack().getType(), amount))));

                if (!leftOver.isEmpty()){ // Checks for inventory space
                    player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getFullInventory());

                    if (voidOnFullInv) {
                        i.remove();
                    }

                    return;
                } else e.setCancelled(false);







                if (doBlacklist) { // Checks if blacklist is enabled
                    if (blacklist.contains(drop.getType().toString())) { // Stops resets the loop skipping the item & not removing it
                        continue;
                    }
                }

                if (doSmelt) {
                    player.getInventory().addItem(AutoSmelt.smelt(drop, player));
                } else {
                    player.getInventory().addItem(drop);
                }
                i.remove();
            }

        }

    }
}
