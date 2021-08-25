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

public class CrateCreateGui implements Listener {

    public Inventory inventory;

    public Player player;

    public String name;

    public CrateCreateGui(Player player, String name)
    {
        this.player=player;
        this.name=name;

        inventory = Bukkit.createInventory(null, 27, "Create Confirmation");

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

        inventory.setItem(12,yes);
        inventory.setItem(14,no);
    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event)
    {
        if(player.equals(event.getWhoClicked()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event)
    {
        if(inventory.equals(event.getInventory()))
        {
            if(event.getRawSlot()==14)
            {
                player.sendMessage(SeniorConfig.crateCreateCancelMessage);
                Bukkit.getScheduler().runTask(SeniorCrates.instance, new Runnable() {
                    @Override
                    public void run() {
                        player.closeInventory();
                    }
                });
            }
            if(event.getRawSlot()==12)
            {
                Crate crate = new Crate(name);
                crate.addReward(new CrateReward(100, player.getInventory().getItemInHand()));
                Crate.crates.add(crate);

                Bukkit.getScheduler().runTask(SeniorCrates.instance, new Runnable() {
                    @Override
                    public void run() {
                        player.closeInventory();
                    }
                });
                player.sendMessage(SeniorConfig.crateCreateConfirmMessage);
            }
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if(event.getInventory().equals(inventory))
        {
            HandlerList.unregisterAll(this);
        }
    }

}
