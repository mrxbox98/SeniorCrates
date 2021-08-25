package me.mrxbox98.SeniorCrates.crates;

import me.mrxbox98.SeniorCrates.SeniorConfig;
import me.mrxbox98.SeniorCrates.SeniorCrates;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Random;

public class CrateOpenGui implements Listener {

    public Inventory inventory;

    public Player player;

    public Crate crate;

    /**
     * Creates the crate opening GUI
     * @param player the player that is opening the gui
     * @param crate the crate that is being opened
     */
    public CrateOpenGui(Player player, Crate crate, ItemStack item)
    {
        this.player=player;

        this.crate=crate;

        inventory = Bukkit.createInventory(null, 45, crate.name);

        openConfirmationMenu();

        player.openInventory(inventory);
    }

    /**
     * Opens the confirmation menu
     */
    public void openConfirmationMenu()
    {
        ItemStack no = new ItemStack(Material.getMaterial("WOOL"),1, (byte)14);

        ItemMeta noMeta = no.getItemMeta();

        noMeta.setDisplayName(ChatColor.RED.toString()+ChatColor.BOLD+"Cancel");

        no.setItemMeta(noMeta);

        ItemStack yes = new ItemStack(Material.getMaterial("WOOL"),1, (byte)5);

        ItemMeta yesMeta = yes.getItemMeta();

        yesMeta.setDisplayName(ChatColor.GREEN.toString()+ChatColor.BOLD+"Confirm");

        yes.setItemMeta(yesMeta);

        inventory.setItem(32,no);
        inventory.setItem(30,yes);


    }

    /**
     * Creates the gui animation
     */
    public void animate()
    {
        inventory.clear();

        ItemStack reward;
        if(crate.roll()==null)
        {
            reward=new ItemStack(Material.AIR);
        }
        else
        {
            reward=crate.roll().getReward();
        }

        for(int i=0;i<(inventory.getSize()-1)/2;i++)
        {
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(SeniorCrates.instance, new Runnable() {
                @Override
                public void run() {
                    inventory.setItem(finalI,generateRandomColorGlassPane());
                    inventory.setItem(inventory.getSize()-finalI-1,generateRandomColorGlassPane());
                }
            },4*i);

            if(i==((inventory.getSize()-1)/2)-1)
            {
                Bukkit.getScheduler().runTaskLater(SeniorCrates.instance, new Runnable() {
                    @Override
                    public void run() {
                        inventory.setItem(22, reward);
                        player.getInventory().addItem(reward);
                        try {
                            SeniorCrates.sqlData.insertData(player);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                },4*i+4);
            }
        }


    }

    /**
     * Generates a random color glass pane
     * @return the itemstack of the glass pane
     */
    public ItemStack generateRandomColorGlassPane() {
        int color = new Random().nextInt(16);
        ItemStack item;

        item=new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(byte)color);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE+"...");
        item.setItemMeta(meta);
        return item;
    }



    /**
     * Prevents the user from dragging items into their inventory
     * @param event the event to cancel
     */
    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event)
    {
        if(player.equals(event.getWhoClicked()))
        {
            event.setCancelled(true);
        }
    }

    /**
     * Processes a click inside the inventory
     * @param event the event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if(!event.getInventory().equals(inventory))
        {
            return;
        }

        if(event.getCurrentItem()==null)
        {
            return;
        }

        if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Confirm"))
        {
            event.setCancelled(true);
            try {
                if(!player.hasPermission("crates.bypass"))
                {

                    if(SeniorCrates.sqlData.cratesPerDay(player))
                    {
                        Bukkit.getScheduler().runTask(SeniorCrates.instance, new Runnable() {
                            @Override
                            public void run() {
                                player.closeInventory();
                            }
                        });
                        player.sendMessage(SeniorConfig.tooManyCratesPerDay);
                        return;
                    }

                    if(SeniorCrates.hashMap.containsKey(player))
                    {
                        Bukkit.getScheduler().runTask(SeniorCrates.instance, new Runnable() {
                            @Override
                            public void run() {
                                player.closeInventory();
                            }
                        });
                        player.sendMessage(SeniorConfig.onCooldown);
                        return;
                    }
                }
                animate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Cancel"))
        {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(SeniorCrates.instance, new Runnable() {
                @Override
                public void run() {
                    player.closeInventory();
                }
            });
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if(event.getInventory().equals(inventory))
        {
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event)
    {
        if(event.getInventory().equals(inventory))
        {
            event.setCancelled(true);
        }
    }
}
