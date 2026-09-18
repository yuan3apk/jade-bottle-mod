package com.yuan.test.datagen;

import com.yuan.test.TestMod;
import com.yuan.test.block.JadenBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import javax.security.auth.callback.LanguageCallback;

public class ModEnLangProvider extends FabricLanguageProvider {
    public ModEnLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(TestMod.JADE_BOTTLE, " jade bottle");
        translationBuilder.add(TestMod.JADE_BOTTLE_CARD, " jade bottle card");
        translationBuilder.add(JadenBlocks.JADEN_BLOCK, " jade block");
    }
}
