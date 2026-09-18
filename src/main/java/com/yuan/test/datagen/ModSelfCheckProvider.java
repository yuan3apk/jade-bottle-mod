package com.yuan.test.datagen;

import com.yuan.test.item.JadeBottleItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.fluid.Fluids;

import java.util.concurrent.CompletableFuture;

/**
 * 自检：玉净瓶的“液体 → 液体方块 / 炼药锅”映射。跑 runDatagen 时顺便验证，改错了会直接报错。
 */
public class ModSelfCheckProvider implements DataProvider {
    public ModSelfCheckProvider(FabricDataOutput output) {
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        check(JadeBottleItem.cauldronFor(Fluids.WATER) != null
                && JadeBottleItem.cauldronFor(Fluids.WATER).isOf(Blocks.WATER_CAULDRON)
                && JadeBottleItem.cauldronFor(Fluids.WATER).get(LeveledCauldronBlock.LEVEL) == LeveledCauldronBlock.MAX_LEVEL,
                "水应映射到满水锅");
        check(JadeBottleItem.cauldronFor(Fluids.LAVA) != null
                && JadeBottleItem.cauldronFor(Fluids.LAVA).isOf(Blocks.LAVA_CAULDRON),
                "岩浆应映射到岩浆锅");
        check(JadeBottleItem.cauldronFor(Fluids.FLOWING_LAVA) == null, "流动岩浆不算瓶装液体");
        check(Fluids.WATER.getDefaultState().getBlockState().getBlock() == Blocks.WATER, "水源方块映射");
        check(Fluids.LAVA.getDefaultState().getBlockState().getBlock() == Blocks.LAVA, "岩浆源方块映射");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "Jade Bottle self-check";
    }

    private static void check(boolean condition, String what) {
        if (!condition) {
            throw new AssertionError("玉净瓶自检失败: " + what);
        }
    }
}
