package com.yuan.test.datagen;

import com.yuan.test.TestMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.MinecartItem;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModRecipesProvider extends FabricRecipeProvider {
    public ModRecipesProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, TestMod.JADE_BOTTLE, 1)
                .pattern("BBB")
                .pattern("BAB")
                .pattern("BBB")
                .input('A', TestMod.JADE_BOTTLE_CARD)
                .input('B', Items.DIAMOND)
                .criterion(hasItem(TestMod.JADE_BOTTLE_CARD), conditionsFromItem(TestMod.JADE_BOTTLE_CARD))
                .offerTo(exporter, new Identifier(TestMod.MOD_ID, "jade_bottle"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, TestMod.LAVA_JADE_BOTTLE, 1)
                .pattern("BBB")
                .pattern("BAB")
                .pattern("BBB")
                .input('A', TestMod.LAVA_JADE_BOTTLE_CARD)
                .input('B', Items.DIAMOND)
                .criterion(hasItem(TestMod.LAVA_JADE_BOTTLE_CARD), conditionsFromItem(TestMod.LAVA_JADE_BOTTLE_CARD))
                .offerTo(exporter, new Identifier(TestMod.MOD_ID, "lava_jade_bottle"));
    }
}
