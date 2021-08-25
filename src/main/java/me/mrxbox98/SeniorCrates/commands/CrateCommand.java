package me.mrxbox98.SeniorCrates.commands;

import me.mrxbox98.SeniorCrates.SeniorConfig;
import me.mrxbox98.SeniorCrates.SeniorCrates;
import me.mrxbox98.SeniorCrates.crates.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class CrateCommand implements CommandExecutor {

    public static String helpCommand = "/crate create <name> - to create a crate\n"+
            "/crate delete <name> - to delete a crate\n"+
            "/crate edit <name> - to edit a crate";

    /**
     * The main command for senior crates
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof ConsoleCommandSender)
        {
            sender.sendMessage("Error: You may only send crates commands as a player!");
        }

        Player player = (Player) sender;

        if(args.length==0)
        {
            player.sendMessage(helpCommand);
            return true;
        }

        if(args[0].equalsIgnoreCase("create"))
        {
            if(!player.hasPermission("crates.admin.create"))
            {
                player.sendMessage(SeniorConfig.noPermissions);
                return true;
            }

            if(player.getInventory().getItemInHand()==null || player.getInventory().getItemInHand().getType()== Material.AIR)
            {
                player.sendMessage(SeniorConfig.crateCreateNoItemMessage);
                return true;
            }
            if(args.length==2)
            {
                for(Crate crate: Crate.crates)
                {
                    if(crate.name.equalsIgnoreCase(args[1]))
                    {
                        player.sendMessage(SeniorConfig.nameTakenError);
                        return true;
                    }
                }
                SeniorCrates.instance.getServer().getPluginManager().registerEvents(new CrateCreateGui(player, args[1]), SeniorCrates.instance);
            }
            else
            {
                player.sendMessage(SeniorConfig.crateNoNameMessage);
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("give"))
        {
            if(!player.hasPermission("crates.admin.give"))
            {
                player.sendMessage(SeniorConfig.noPermissions);
                return true;
            }

            if(args.length>=3)
            {
                for(Crate crate: Crate.crates)
                {
                    if(crate.name.equalsIgnoreCase(args[2]))
                    {
                        if(args.length==3)
                        {
                            SeniorCrates.instance.getServer().getPlayer(args[1]).getInventory().addItem(new CrateItem(crate));
                        }
                        else
                        {
                            try
                            {
                                SeniorCrates.instance.getServer().getPlayer(args[1]).getInventory().addItem(new CrateItem(crate,Integer.parseInt(args[3])));
                            }
                            catch(NumberFormatException e)
                            {
                                player.sendMessage(SeniorConfig.numberFormatError);
                            }
                        }
                    }
                }
            }
            else
            {
                player.sendMessage(SeniorConfig.crateNoNameMessage);
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("edit"))
        {
            if(!player.hasPermission("crates.admin.edit"))
            {
                player.sendMessage(SeniorConfig.noPermissions);
                return true;
            }

            if(args.length==2)
            {
               for(Crate crate: Crate.crates)
               {
                   if(crate.name.equalsIgnoreCase(args[1]))
                   {
                       SeniorCrates.instance.getServer().getPluginManager().registerEvents(new CrateEditGui(player, crate), SeniorCrates.instance);
                   }
               }
            }
            else
            {
                player.sendMessage(SeniorConfig.crateNoNameMessage);
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("delete"))
        {
            if(!player.hasPermission("crates.admin.delete"))
            {
                player.sendMessage(SeniorConfig.noPermissions);
                return true;
            }

            if(args.length==2)
            {
                SeniorCrates.instance.getServer().getPluginManager().registerEvents(new CrateDeleteGui(player,args[2]),SeniorCrates.instance);
            }
            else
            {
                player.sendMessage(SeniorConfig.crateNoNameMessage);
                return true;
            }
        }



        return true;
    }
}
