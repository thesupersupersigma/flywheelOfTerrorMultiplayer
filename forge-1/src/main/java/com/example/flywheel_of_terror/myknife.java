package com.example.flywheel_of_terror;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;

public class myknife extends SwordItem {
   public myknife() {
      super(Tiers.IRON, 13, -2.9F, new Properties().durability(400000).rarity(Rarity.UNCOMMON));
   }
}
