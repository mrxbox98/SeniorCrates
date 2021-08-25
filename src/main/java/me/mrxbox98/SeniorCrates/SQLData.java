package me.mrxbox98.SeniorCrates;

import me.mrxbox98.SeniorCrates.crates.Crate;
import me.mrxbox98.SeniorCrates.crates.CrateReward;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;

public class SQLData {

    Connection connection;

    /**
     * Creates a connection to the sql database and throws an error if it cannot log in
     * @throws SQLException thrown if there is an error while logging in
     */
    public SQLData() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://"+
                SeniorConfig.host+":"+
                SeniorConfig.port+"/"+
                SeniorConfig.database,
                SeniorConfig.username,
                SeniorConfig.password);
    }

    /**
     * An SQL command
     * @param q the command to send
     * @return the return statement
     */
    public PreparedStatement preparedStatement(String q)
    {
        PreparedStatement statement=null;

        try
        {
            statement = connection.prepareStatement(q);
        }
        catch(SQLException e)
        {
            SeniorCrates.instance.getLogger().warning("SQL Error");
            e.printStackTrace();
        }
        return statement;
    }

    /**
     * Sets up a table for a crate
     * @param crate the crate that is being saved
     */
    public void setupTable(Crate crate)
    {
        try {

            if(hasTable(generateTableName(crate)))
            {
                String command = "DELETE FROM "+generateTableName(crate)+";";
                preparedStatement(command).executeUpdate();


            }
            else
            {
                String command = "";
                command+="CREATE TABLE "+ generateTableName(crate);
                command+=" (materialname TEXT, count INT, chance DOUBLE);";
                try {
                    preparedStatement(command).executeUpdate();
                } catch (SQLException e) {
                    SeniorCrates.instance.getLogger().warning("SQL Error");
                    e.printStackTrace();
                }
            }

            for(CrateReward crateReward: crate.rewards)
            {
                String command="";
                command+="INSERT INTO "+generateTableName(crate)+" (materialname, count, chance)";
                command+="VALUES ("+"'"+crateReward.getReward().getType().name()+"',"+crateReward.getReward().getAmount()+","+crateReward.getChance()+")";
                try {
                    preparedStatement(command).executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    /**
     * Loads a crate from the mysql table
     * @throws SQLException thrown if there is a sql error
     */
    public void loadCrates() throws SQLException {
        ResultSet resultSet = preparedStatement("SHOW TABLES;").executeQuery();
        while(resultSet.next())
        {
            if(resultSet.getString(1).equalsIgnoreCase("DefaultDataTable"))
            {
                continue;
            }
            Crate crate;

            String name=resultSet.getString(1).substring(0,resultSet.getString(1).indexOf("_"));

            int id = Integer.valueOf(resultSet.getString(1).substring(resultSet.getString(1).indexOf("_")+1));

            crate = new Crate(name);

            crate.setId(id);

            ResultSet resultSet1 = preparedStatement("SELECT * FROM "+resultSet.getString(1)+";").executeQuery();
            while(resultSet1.next())
            {
                crate.rewards.add(new CrateReward(Double.parseDouble(resultSet1.getString(3)),new ItemStack(Material.getMaterial(resultSet1.getString(1)), Integer.parseInt(resultSet1.getString(2)))));
            }

            Crate.crates.add(crate);
        }
    }

    /**
     * Deletes a crate from the sql table
     * @param crate the crate  to delete
     * @throws SQLException throw if there is a sql error
     */
    public void deleteCrate(Crate crate) throws SQLException {
        preparedStatement("DROP "+ generateTableName(crate)+";").execute();
    }

    public String generateTableName(Crate crate)
    {
        return crate.name+"_"+crate.id;
    }

    public boolean hasTable(String name) throws SQLException {
        ResultSet resultSet = preparedStatement("SHOW TABLES;").executeQuery();
        while(resultSet.next())
        {
            if(name.equalsIgnoreCase(resultSet.getString(1)))
            {
                return true;
            }
        }
        return false;
    }

    public void setupDataTable() throws SQLException {
        if(!hasTable("DefaultDataTable"))
        {
            preparedStatement("CREATE TABLE DefaultDataTable (date DATETIME, uuid TEXT);").executeUpdate();
        }

    }

    public void insertData(Player player) throws SQLException {
        String str= "INSERT INTO DefaultDataTable (date, uuid)";
        str+="VALUES(CURRENT_TIMESTAMP,'"+player.getUniqueId().toString()+"');";

        preparedStatement(str).executeUpdate();
    }

    public boolean cratesPerDay(Player player) throws SQLException {
        ResultSet resultSet = preparedStatement("SELECT date FROM DefaultDataTable WHERE uuid='"+player.getUniqueId().toString()+"';").executeQuery();
        int count=0;
        while(resultSet.next())
        {


            Date date = resultSet.getDate(1);
            java.util.Date utilDate = date;
            long diff = utilDate.getTime()-new java.util.Date().getTime();
            diff = Math.abs(diff);
            if(diff<=24*60*60*1000)
            {
                count++;
            }
        }
        return count>=SeniorConfig.cratesPerDay;
    }
}
