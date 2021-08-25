package me.mrxbox98.SeniorCrates;

import me.mrxbox98.SeniorCrates.commands.CrateCommand;
import me.mrxbox98.SeniorCrates.crates.Crate;
import me.mrxbox98.SeniorCrates.listeners.ItemListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

public class SeniorCrates extends JavaPlugin {

    public static JavaPlugin instance;

    public static String mcVersion="";

    public static SQLData sqlData;

    public static HashMap<Player, Date> hashMap = new HashMap<>();

    @Override
    public void onEnable()
    {
        instance=this;
        getCommand("crate").setExecutor(new CrateCommand());
        setMcVersion();
        SeniorConfig.setupConfig();
        getServer().getPluginManager().registerEvents(new ItemListener(),this);

        try
        {
            sqlData = new SQLData();
            sqlData.loadCrates();
            sqlData.setupDataTable();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                hashMap.forEach((k,v) -> {
                    if(v.compareTo(new Date())>=SeniorConfig.cooldown*1000)
                    {
                        hashMap.remove(k);
                    }
                });
            }
        },0,20);
    }

    @Override
    public void onDisable()
    {
        for(Crate crate: Crate.crates)
        {
            sqlData.setupTable(crate);
        }
    }

    /**
     * Detects the Minecraft version the server is running on
     */
    public void setMcVersion()
    {
        if(getServer().getVersion().contains("1.8")){mcVersion="1.8";}
        if(getServer().getVersion().contains("1.9")){mcVersion="1.9";}
        if(getServer().getVersion().contains("1.10")){mcVersion="1.10";}
        if(getServer().getVersion().contains("1.11")){mcVersion="1.11";}
        if(getServer().getVersion().contains("1.12")){mcVersion="1.12";}
        if(getServer().getVersion().contains("1.13")){mcVersion="1.13";}
        if(getServer().getVersion().contains("1.14")){mcVersion="1.14";}
        if(getServer().getVersion().contains("1.15")){mcVersion="1.15";}
        if(getServer().getVersion().contains("1.16")){mcVersion="1.16";}
        if(getServer().getVersion().contains("1.17")){mcVersion="1.17";}
        if(mcVersion.equals(""))
        {
            getLogger().warning("Unable to detect MC version!");
        }
    }
}
