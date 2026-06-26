package com.example.flywheel_of_terror;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class add_items {
   public static final DeferredRegister<Item> do_item = DeferredRegister.create(ForgeRegistries.ITEMS, "flywheel_of_terror");
   public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "flywheel_of_terror");
   public static final RegistryObject<Item> my_knife = do_item.register("my_knife", () -> new myknife());
   public static final RegistryObject<Item> notice = do_item.register("notice", () -> new Item(new Properties()));
   public static final RegistryObject<Item> punishment = do_item.register("punishment", () -> new punishment());
   public static final RegistryObject<Item> your_legacy = do_item.register("your_legacy", () -> new Item(new Properties()));
}
