package com.yuan.test.item;

import com.yuan.test.TestMod;
import com.yuan.test.block.JadenBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ObjectUtils;

public class JadeBottleGroups {
    public static final RegistryKey<ItemGroup> JADE_BOTTLE_GROUP = register("jade_bottle_group");
    private static RegistryKey<ItemGroup> register(String id) {
        return RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(TestMod.MOD_ID, id));
    }
    public static void registerGroups() {
        Registry.register(
                Registries.ITEM_GROUP,
                JADE_BOTTLE_GROUP,
                ItemGroup.create(null, -1)
                        .displayName(Text.translatable("itemGroup.jade_bottle_group"))
                        .icon(() -> new ItemStack(TestMod.JADE_BOTTLE))
                        .entries((displayContext, entries) ->{
                            entries.add(TestMod.JADE_BOTTLE);
                            entries.add(TestMod.LAVA_JADE_BOTTLE);
                            entries.add(JadenBlocks.JADEN_BLOCK);
                            entries.add(TestMod.JADE_BOTTLE_CARD);
                            entries.add(TestMod.LAVA_JADE_BOTTLE_CARD);
                        })
                        .build()
        );
    }
}
