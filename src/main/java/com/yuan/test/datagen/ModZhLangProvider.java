package com.yuan.test.datagen;

import com.yuan.test.TestMod;
import com.yuan.test.block.JadenBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModZhLangProvider extends FabricLanguageProvider {
    public ModZhLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "zh_cn");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(TestMod.JADE_BOTTLE, " 玉净");
        translationBuilder.add(TestMod.JADE_BOTTLE_CARD, " 玉净瓶碎片");
        translationBuilder.add(JadenBlocks.JADEN_BLOCK, " 玉块");
    }
}
