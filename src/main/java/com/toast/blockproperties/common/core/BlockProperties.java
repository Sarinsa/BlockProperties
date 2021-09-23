package com.toast.blockproperties.common.core;

import com.toast.blockproperties.common.core.registry.BPItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BlockProperties.MODID)
public class BlockProperties {

    public static final String MODID = "blockproperties";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public BlockProperties() {

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BPItems.ITEMS.register(eventBus);
    }
}
