package com.yuan.test.datagen;

import com.yuan.test.TestMod;
import com.yuan.test.block.JadenBlocks;
import com.yuan.test.item.JadeBottleGroups;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModZhLangProvider extends FabricLanguageProvider {
    public ModZhLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "zh_cn");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(TestMod.JADE_BOTTLE, " 玉净瓶(水)");
        translationBuilder.add(TestMod.JADE_BOTTLE_CARD, " 玉净瓶(水)碎片");
        translationBuilder.add(TestMod.LAVA_JADE_BOTTLE, " 玉净瓶(火)");
        translationBuilder.add(TestMod.LAVA_JADE_BOTTLE_CARD, " 玉净瓶(火)碎片");
        translationBuilder.add(JadenBlocks.JADEN_BLOCK, " 玉块");
        translationBuilder.add(JadeBottleGroups.JADE_BOTTLE_GROUP, " 玉净瓶组");
    }
}
