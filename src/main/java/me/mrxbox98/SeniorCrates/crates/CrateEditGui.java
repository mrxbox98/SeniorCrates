package me.mrxbox98.SeniorCrates.crates;

import me.mrxbox98.SeniorCrates.SeniorConfig;
import me.mrxbox98.SeniorCrates.SeniorCrates;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

public class CrateEditGui implements Listener {

    public Player player;

    public Inventory inventory;

    public Crate crate;

    public CrateReward selectedReward;

    public boolean listenChat;

    /**
     * Creates a new gui to edit the crate
     * @param player the player
     * @param crate the crate
     */
    public CrateEditGui(Player player, Crate crate) {
        this.player = player;

        this.crate = crate;

        int rows = crate.rewards.size() / 9 + 1;

        inventory = Bukkit.createInventory(null, rows * 9, "Edit crate " + crate.name);

        for (CrateReward reward : crate.rewards)
        {
            inventory.addItem(reward.getReward());
        }

        player.openInventory(inventory);
    }

    /**
     * Called when the inventory is clicked
     * @param event the inventory click event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if((!event.getWhoClicked().equals(player)) || event.getCurrentItem()==null || event.getCurrentItem().getType()==Material.AIR)
        {
            return;
        }

        if(event.isRightClick() && !(event.getClickedInventory().getType()==InventoryType.PLAYER))
        {
            event.setCancelled(true);
            inventory.setItem(event.getRawSlot(),null);
            crate.rewards.remove(event.getRawSlot());
        }

        if(event.isLeftClick() && event.getClickedInventory().getType()==InventoryType.PLAYER)
        {
            event.setCancelled(true);
            if(inventory.firstEmpty()==-1)
            {
                return;
            }
            if(inventory.firstEmpty()>=SeniorConfig.itemsInCrate)
            {
                player.sendMessage(SeniorConfig.tooManyItems);
                return;
            }
            inventory.setItem(inventory.firstEmpty(),event.getCurrentItem());
            crate.rewards.add(new CrateReward(10d,event.getCurrentItem()));
        }

        if(event.isShiftClick() && !(event.getClickedInventory().getType()==InventoryType.PLAYER))
        {
            event.setCancelled(true);
            listenChat=true;

            selectedReward=crate.rewards.get(event.getSlot());

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
    public void onInventoryDragEvent(InventoryDragEvent event)
    {
        if(player.equals(event.getWhoClicked()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAsyncChatEvent(AsyncPlayerChatEvent event)
    {
        if(listenChat)
        {
            if(player.equals(event.getPlayer()))
            {
                try
                {
                    double chance = Double.parseDouble(event.getMessage());
                    if(chance>100 || chance<0)
                    {
                        throw new NumberFormatException();
                    }
                    selectedReward.setChance(chance);
                    player.sendMessage(SeniorConfig.crateEditChanceMessage.replace("%item_name%",selectedReward.getReward().getType().name()).replace("%item_chance%",String.valueOf(chance)));
                    listenChat=false;
                    HandlerList.unregisterAll(this);
                }
                catch(NumberFormatException e)
                {
                    event.getPlayer().sendMessage(SeniorConfig.badNumberError);
                }
                event.setCancelled(true);
            }
        }
        else
        {
            return;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if(event.getInventory().equals(inventory) && listenChat==false)
        {
            HandlerList.unregisterAll(this);
        }
    }
}
