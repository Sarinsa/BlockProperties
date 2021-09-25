package com.toast.blockproperties.client.screen.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.toast.blockproperties.client.ClientUtil;
import com.toast.blockproperties.common.core.registry.BPItems;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class BlockConfigEntry extends AbstractList.AbstractListEntry<BlockConfigEntry> {

    private final ResourceLocation registryName;

    private final String displayName;
    private final ItemStack renderStack;
    private final Block block;

    private final BlockListConfigScreen.BlockEntryList entryList;
    private final BlockListConfigScreen screen;


    public BlockConfigEntry(Block block, String displayName, BlockListConfigScreen.BlockEntryList entryList, BlockListConfigScreen screen) {
        this.block = block;
        this.registryName = block.getRegistryName();
        this.displayName = displayName;

        this.entryList = entryList;
        this.screen = screen;

        this.renderStack = ClientUtil.getConfigRenderStack(block);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    public Block getBlock() {
        return this.block;
    }

    public ItemStack getRenderStack() {
        return this.renderStack;
    }

    @Override
    public void render(MatrixStack matrixStack, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int mouseX, int mouseY, boolean isMouseOver, float p_230432_10_) {
        Minecraft.getInstance().font.drawShadow(matrixStack, this.displayName, this.entryList.getRowLeft() + 24, (p_230432_3_ + 3), 16777215, true);

        int x = this.entryList.getRowLeft();
        int y = p_230432_3_ - 1;

        Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(this.renderStack, x, y);
    }

    @Override
    public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
        if (p_231044_5_ == 0) {
            this.select(this);
            return true;
        }
        else {
            return false;
        }
    }

    public void select(BlockConfigEntry entry) {
        this.screen.openBlockConfig(entry);
    }
}
