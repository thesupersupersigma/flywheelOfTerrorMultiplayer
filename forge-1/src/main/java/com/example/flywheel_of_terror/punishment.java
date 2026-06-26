package com.example.flywheel_of_terror;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;

public class punishment extends SwordItem {
   public punishment() {
      super(Tiers.IRON, 57, -0.4F, new Properties().durability(1).rarity(Rarity.UNCOMMON));
   }
}
