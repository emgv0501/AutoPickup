package us.thezircon.play.autopickup.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.thezircon.play.autopickup.AutoPickup;

public class PlayerQuitEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (PLUGIN.autopickup_list.contains(player)) {
            PLUGIN.autopickup_list.remove(player);
        } else return;
    }



}
