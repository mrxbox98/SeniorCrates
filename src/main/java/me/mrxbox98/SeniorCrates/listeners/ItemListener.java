package me.mrxbox98.SeniorCrates.listeners;

import me.mrxbox98.SeniorCrates.SeniorCrates;
import me.mrxbox98.SeniorCrates.crates.Crate;
import me.mrxbox98.SeniorCrates.crates.CrateOpenGui;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemListener implements Listener {

    @EventHandler
    public void onItemRightClick(PlayerInteractEvent event)
    {
        if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
        {
            return;
        }

        if(event.getItem()!=null)
        {
            for(Crate crate: Crate.crates)
            {
                try
                {

                    int id = Integer.parseInt(event.getItem().getItemMeta().getLore().get(0).replace(" "+ChatColor.COLOR_CHAR,""));

                    if(id==crate.id)
                    {
                        SeniorCrates.instance.getServer().getPluginManager().registerEvents(new CrateOpenGui(event.getPlayer(), crate, event.getItem()), SeniorCrates.instance);
                    }
                }
                catch(Exception | Error ignored)
                {

                }
            }

        }
    }

    /**
     * Prevents the crate from being placed
     * @param event the event fired
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        try
        {
            if(event.getItemInHand().getType().equals(Material.ENDER_CHEST) && event.getItemInHand().getItemMeta().getDisplayName().contains("Crate"))
            {
                event.setCancelled(true);
            }
        }
        catch(Error | Exception ignored)
        {

        }
    }

}
