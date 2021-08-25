package me.mrxbox98.SeniorCrates;

import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;

public class SeniorConfig {

    public static int cratesPerDay=10;

    //In Seconds
    public static int cooldown=10;

    public static int itemsInCrate=9;

    public static String host = "localhost";

    public static int port = 3306;

    public static String database = "SeniorCrates";

    public static String username = "root";

    public static String password = "";

    public static String crateCreateConfirmMessage="You have successfully created a new crate!";

    public static String crateCreateNoItemMessage="You have successfully created a new crate!";

    public static String crateCreateCancelMessage="You have successfully created a new crate!";

    public static String crateDeleteConfirmMessage="You have deleted this crate!";

    public static String crateDeleteCancelMessage="You have canceled creating your crate!";

    public static String crateNoNameMessage="You must specify a name!";

    public static String badNumberError="You must enter a positive number 100 or under!";

    public static String nameTakenError="This name has already been taken!";

    public static String numberFormatError="You must enter a number!";

    public static String crateEditChanceMessage="You set the chance of getting %item_name% to %item_chance%";

    public static String noPermissions="You do not have permission to use this command!";

    public static String tooManyCratesPerDay="You have reached the maximum crates per day!";

    public static String onCooldown="You are currently on cooldown!";

    public static String tooManyItems="You cannot add any items to this crate!";

    public static FileConfiguration config;

    /**
     * Sets up the config.yml with java reflection
     */
    public static void setupConfig()
    {
        config=SeniorCrates.instance.getConfig();

        Class clzz = SeniorConfig.class;

        for(Field field: clzz.getFields())
        {
            String name = field.getName();

            if(name.equalsIgnoreCase("config"))
            {
                continue;
            }

            name=name.substring(0,1).toUpperCase()+name.substring(1);

            if(!config.contains(name))
            {
                try {
                    if(field.getType().equals(int.class))
                    {
                        config.addDefault(name, field.getInt(null));
                    }
                    else
                    {
                        config.addDefault(name, field.get(null));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            try
            {
                if(field.getType().equals(int.class))
                {
                    field.setInt(null, config.getInt(name));
                }
                else
                {
                    field.set(null, config.getString(name));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        config.options().copyDefaults(true);

        SeniorCrates.instance.saveConfig();
    }



}
