package me.mrxbox98.SeniorCrates.crates;

import org.bukkit.inventory.ItemStack;

public class CrateReward {

    private double chance;

    private ItemStack reward;

    public CrateReward(double chance, ItemStack reward)
    {
        this.chance=chance;
        this.reward=reward.clone();
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public ItemStack getReward()
    {
        return reward;
    }
}
