package com.yuan.test;

import com.yuan.test.block.JadenBlocks;
import com.yuan.test.item.JadeBottleCard;
import com.yuan.test.item.JadeBottleGroups;
import com.yuan.test.item.JadeBottleItem;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMod implements ModInitializer {
	public static final String MOD_ID = "test-mod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Item JADE_BOTTLE = new JadeBottleItem(
		new Item.Settings().maxCount(1)
	);
	public static final Item JADE_BOTTLE_CARD = new JadeBottleCard(
		new Item.Settings().maxCount(16)
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Registry.register(Registries.ITEM, id("jade_bottle"), JADE_BOTTLE);
		Registry.register(Registries.ITEM, id("jade_bottle_card"), JADE_BOTTLE_CARD);

		JadeBottleGroups.registerGroups();

		JadenBlocks.registerModBlocks();

		LOGGER.info("Jade Bottle mod initialized!");
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
