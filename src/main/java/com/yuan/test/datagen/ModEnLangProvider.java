package com.yuan.test.datagen;

import com.yuan.test.TestMod;
import com.yuan.test.block.JadenBlocks;
import com.yuan.test.item.JadeBottleGroups;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import javax.security.auth.callback.LanguageCallback;

public class ModEnLangProvider extends FabricLanguageProvider {
    public ModEnLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(TestMod.JADE_BOTTLE, " jade bottle (water)");
        translationBuilder.add(TestMod.JADE_BOTTLE_CARD, " jade bottle (water) card");
        translationBuilder.add(TestMod.LAVA_JADE_BOTTLE, " jade bottle (lava)");
        translationBuilder.add(TestMod.LAVA_JADE_BOTTLE_CARD, " jade bottle (lava) card");
        translationBuilder.add(JadenBlocks.JADEN_BLOCK, " jade block");
        translationBuilder.add(JadeBottleGroups.JADE_BOTTLE_GROUP, " jade bottle group");
    }
}
