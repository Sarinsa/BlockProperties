package com.toast.blockproperties.client;

import com.toast.blockproperties.common.core.registry.BPItems;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ClientUtil {

    private static final Map<Fluid, ResourceLocation> FLUID_TEXTURES = new LinkedHashMap<>();

    // Utility class, no point in instantiating
    private ClientUtil() {}


    // Unused for now. May come in handy if we decide to render fluid blocks
    // as flat squares of their source texture rather than fluid buckets.
    @Nonnull
    public static <T extends Fluid> ResourceLocation getFromFluid(T fluid) {
        return FLUID_TEXTURES.get(fluid);
    }

    public static void collectFluidTextures() {
        FLUID_TEXTURES.clear();

        for (Fluid fluid : ForgeRegistries.FLUIDS) {
            ResourceLocation texture = fluid.getAttributes().getStillTexture();

            if (texture == null) {
                texture = new ResourceLocation("");
            }
            FLUID_TEXTURES.put(fluid, texture);
        }
    }

    /**
     * Creates an ItemStack to be used as an icon for block
     * config entries in our config GUI.
     *
     * @param block The block to get an icon ItemStack for.
     * @return A new ItemStack to use for rendering an icon.
     *
     *         // FLUID
     *         If the block is a fluid, this will attempt to
     *         return an ItemStack of the fluid's bucket item.
     *
     *         // MISSING MODEL
     *         If the block's block item does not have a registered
     *         item model, BlockProperties' missing icon item will be used.
     *
     *         // DEFAULT
     *         Returns an ItemStack of the block's block item
     *         if the block item has a registered item model.
     */
    public static ItemStack getConfigRenderStack(Block block) {
        if (block instanceof FlowingFluidBlock) {
            Fluid fluid = ((FlowingFluidBlock) block).getFluid();
            return new ItemStack(fluid.getBucket());
        }
        else {
            if (Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(block.asItem()) == null) {
                return new ItemStack(BPItems.MISSING_ITEM_ICON.get());
            }
            else {
                return new ItemStack(block.asItem());
            }
        }
    }
}
