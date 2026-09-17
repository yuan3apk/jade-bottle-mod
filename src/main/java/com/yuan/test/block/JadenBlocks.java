package com.yuan.test.block;

import com.yuan.test.TestMod;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

public class JadenBlocks extends Block {
    public JadenBlocks(Settings settings) {
        super(settings);
    }
    public static final Block JADEN_BLOCK = register("jaden_block", new Block(Block.Settings.copy(Blocks.STONE)));
    public static Block register(String id, Block block)
    {
        registerBlockItems(id, block);
        return Registry.register(Registries.BLOCK, new Identifier(TestMod.MOD_ID, id), block);
    }
    public static void registerBlockItems(String id, Block block)
    {
        Registry.register(Registries.ITEM, new Identifier(TestMod.MOD_ID, id), new BlockItem(block, new Item.Settings()));
    }
    public static void registerModBlocks()
    {
    }
}
