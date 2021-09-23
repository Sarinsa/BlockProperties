package com.toast.blockproperties.common.core.registry;

import com.toast.blockproperties.common.core.BlockProperties;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BPItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BlockProperties.MODID);


    public static final RegistryObject<Item> MISSING_ITEM_ICON = ITEMS.register("missing_item_icon", () -> new Item(new Item.Properties().stacksTo(1)));
}
