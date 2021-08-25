package me.mrxbox98.SeniorCrates.crates;

import java.util.ArrayList;
import java.util.Random;

public class Crate {

    public static ArrayList<Crate> crates = new ArrayList<>();

    //The possible rewards for the crate
    public ArrayList<CrateReward> rewards = new ArrayList<>();

    public int id;

    public String name;

    public Crate(String name) {
        this.name=name;
        id=Math.abs(new Random().nextInt());
    }

    public void setId(int id)
    {
        this.id=id;
    }

    /**
     * Adds a reward to the crate.
     * @param crateReward
     */
    public void addReward(CrateReward crateReward)
    {
        rewards.add(crateReward);
    }

    /**
     * Goes through the rewards arraylist and checks
     * whether the roll is within the stack and the chance.
     * The stack increases when a reward is not valid so that a high
     * roll value such as 99 can get a 1% reward.
     */
    public CrateReward roll()
    {
        double roll = new Random().nextDouble()*100D;
        double stack=0d;
        for(CrateReward reward: rewards)
        {
            if(roll>=stack && roll<=stack+reward.getChance())
            {
                return reward;
            }
            else
            {
                stack+=reward.getChance();
            }
        }
        return null;
    }

}
