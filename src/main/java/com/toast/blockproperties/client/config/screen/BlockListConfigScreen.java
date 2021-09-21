package com.toast.blockproperties.client.config.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.toast.blockproperties.common.misc.TranslationStrings;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockListConfigScreen extends Screen {

    /** The previous screen, usually the BlockProperty main config screen. */
    private final Screen parent;

    private BlockEntryList blockEntryList;

    /** The display name of the selected mod */
    private final ITextComponent modName;
    /** The modid of the selected mod */
    private final String modid;


    protected BlockListConfigScreen(Screen parent, MainConfigScreen.ModEntryList.ModEntry modEntry) {
        super(new TranslationTextComponent(TranslationStrings.BLOCK_LIST_SCREEN_TITLE));
        this.parent = parent;
        this.modName = new TranslationTextComponent(modEntry.getModName());
        this.modid = modEntry.getModid();
    }

    @Override
    public void init() {
        this.blockEntryList = new BlockEntryList(this.minecraft, this.modid);
        this.children.add(this.blockEntryList);

        // Done button
        this.addButton(new Button(this.width / 2 - 75, this.height - this.height / 8, 70, 20, new TranslationTextComponent("gui.done"), (button) -> {
            this.minecraft.setScreen(this.parent);
        }));

        // Cancel button
        this.addButton(new Button(this.width / 2 + 5, this.height - this.height / 8, 70, 20, new TranslationTextComponent("gui.cancel"), (button) -> {
            this.minecraft.setScreen(this.parent);
        }));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.modName, this.width / 2, 5, -1);

        this.blockEntryList.render(matrixStack, mouseX, mouseX, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public class BlockEntryList extends ExtendedList<BlockListConfigScreen.BlockEntryList.BlockEntry> {

        public BlockEntryList(Minecraft minecraft, String modid) {
            super(minecraft, BlockListConfigScreen.this.width, BlockListConfigScreen.this.height, 40, BlockListConfigScreen.this.height - 45, 18);

            List<Block> modBlocks = new ArrayList<>();

            ForgeRegistries.BLOCKS.iterator().forEachRemaining((block) -> {
                if (block.getRegistryName().getNamespace().equals(modid))
                    modBlocks.add(block);
            });

            for (Block block : modBlocks) {
                BlockListConfigScreen.BlockEntryList.BlockEntry blockEntry = new BlockListConfigScreen.BlockEntryList.BlockEntry(block.getRegistryName(), block);
                this.addEntry(blockEntry);
            }

            // Remove air block from entries as it is
            // dangerous to mess around with
            if (modid.equals("minecraft")) {
                this.removeEntry(this.getEntry(0));
            }
        }

        /**
         * Opens the block config for the specified block.
         *
         * @param entry The block entry selected from the
         *              block entry list.
         */
        public void openBlockConfig(BlockListConfigScreen.BlockEntryList.BlockEntry entry) {
            // TODO
        }

        @Override
        protected int getScrollbarPosition() {
            return super.getScrollbarPosition() + 20;
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }

        @Override
        public void setSelected(@Nullable BlockListConfigScreen.BlockEntryList.BlockEntry modEntry) {
            super.setSelected(modEntry);
        }

        @Override
        protected void renderBackground(MatrixStack matrixStack) {
            BlockListConfigScreen.this.renderBackground(matrixStack);
        }

        @Override
        protected boolean isFocused() {
            return BlockListConfigScreen.this.getFocused() == this;
        }


        public class BlockEntry extends ExtendedList.AbstractListEntry<BlockListConfigScreen.BlockEntryList.BlockEntry> {

            private final ResourceLocation registryName;
            private final TranslationTextComponent displayName;
            private final Block block;
            private final ItemStack renderStack;

            public BlockEntry(ResourceLocation registryName, Block block) {
                this.registryName = registryName;
                this.displayName = new TranslationTextComponent("block." + this.registryName.getNamespace() + "." + this.registryName.getPath());
                this.block = block;
                this.renderStack = new ItemStack(this.block.asItem());
            }

            @Override
            public void render(MatrixStack matrixStack, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
                String s = this.displayName.getString();
                BlockListConfigScreen.this.font.drawShadow(matrixStack, s, BlockEntryList.this.getRowLeft() + 24, (p_230432_3_ + 3), 16777215, true);

                Minecraft.getInstance().getItemRenderer().renderGuiItem(this.renderStack, BlockEntryList.this.getRowLeft(), (p_230432_3_ - 1));
            }

            public ResourceLocation getRegistryName() {
                return this.registryName;
            }

            public ITextComponent getDisplayName() {
                return this.displayName;
            }

            public Block getBlock() {
                return this.block;
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

            private void select(BlockEntry entry) {
                BlockListConfigScreen.BlockEntryList.this.setSelected(entry);
            }
        }
    }
}
