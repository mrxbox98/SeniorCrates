package me.mrxbox98.SeniorCrates.crates;

import me.mrxbox98.SeniorCrates.SeniorCrates;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class CrateItem extends ItemStack {

    public Crate crate;

    public CrateItem(Crate name)
    {
        super(Material.ENDER_CHEST);
        crate=name;
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(ChatColor.BOLD.toString()+ ChatColor.AQUA+name.name + " Crate");
        ArrayList<String> strings = new ArrayList<>();

        String str="";

        str=String.valueOf(crate.id).replace(""," "+ChatColor.COLOR_CHAR+"");

        SeniorCrates.instance.getLogger().warning(str);

        strings.add(str);
        meta.setLore(strings);
        setItemMeta(meta);
    }

    public CrateItem(Crate name, int count)
    {
        super(Material.ENDER_CHEST, count);
        crate=name;
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(ChatColor.BOLD.toString()+ ChatColor.AQUA+name.name + " Crate");
        ArrayList<String> strings = new ArrayList<>();

        String str="";

        for (char c : String.valueOf(crate.id).toCharArray())
        {
            str += ChatColor.COLOR_CHAR+""+c;
        }

        strings.add(str);
        meta.setLore(strings);
        setItemMeta(meta);
    }

}
