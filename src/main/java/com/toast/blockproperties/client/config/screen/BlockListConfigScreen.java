package com.toast.blockproperties.client.config.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.toast.blockproperties.common.core.BlockProperties;
import com.toast.blockproperties.common.misc.TranslationStrings;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The block list config screen is where the user
 * can search for and pick the block they
 * wish to configure properties for.
 */
public class BlockListConfigScreen extends Screen {

    /** The ResourceLocation for the search icon displayed on the search button */
    private static final ResourceLocation SEARCH_ICON = new ResourceLocation(BlockProperties.MODID, "textures/gui/search_icon.png");

    /** The previous screen, usually the BlockProperty main config screen */
    private final Screen parent;

    /** The modid of the selected mod */
    private final String modid;

    /** A scrollable list containing all the blocks from the chosen mod */
    private BlockEntryList blockEntryList;

    /** A text field for searching for specific blocks */
    private TextFieldWidget searchField;

    /**
     * The last search String that was typed into
     * the search text field. Stored for GUI
     * re-initialization when the game window
     * is resized and whatnot.
     */
    private String lastSearch = "";

    /** The display name of the selected mod */
    private final ITextComponent modName;

    /** A Set containing all the blocks that belong to the specified mod */
    private final List<Block> modBlocks = new ArrayList<>();


    protected BlockListConfigScreen(Screen parent, MainConfigScreen.ModEntryList.ModEntry modEntry) {
        super(new TranslationTextComponent(TranslationStrings.BLOCK_LIST_SCREEN_TITLE));
        this.parent = parent;
        this.modName = new TranslationTextComponent(modEntry.getModName());
        this.modid = modEntry.getModid();

        ForgeRegistries.BLOCKS.iterator().forEachRemaining((block) -> {
            // Exclude any sort of air blocks as it
            // is usually a bad idea to mess around with them.
            if (!(block instanceof AirBlock) && block.getRegistryName().getNamespace().equals(this.modid))
                this.modBlocks.add(block);
        });
    }

    @Override
    public void init() {
        this.searchField = new TextFieldWidget(this.minecraft.font, (this.width / 2) - 40, 13, 80, 14, new TranslationTextComponent("itemGroup.search"));
        this.searchField.setMaxLength(50);
        this.searchField.setBordered(true);
        this.searchField.setVisible(true);
        this.searchField.setTextColor(16777215);
        this.searchField.setValue(this.lastSearch.isEmpty() ? "" : this.lastSearch);
        this.children.add(this.searchField);

        this.blockEntryList = new BlockEntryList(this.minecraft);
        this.children.add(this.blockEntryList);

        // Done button
        this.addButton(new Button(this.width / 2 - 75, this.height - this.height / 8, 70, 20, new TranslationTextComponent("gui.done"), (button) -> {
            this.minecraft.setScreen(this.parent);
        }));

        // Cancel button
        this.addButton(new Button(this.width / 2 + 5, this.height - this.height / 8, 70, 20, new TranslationTextComponent("gui.cancel"), (button) -> {
            this.minecraft.setScreen(this.parent);
        }));

        this.addButton(new ImageButton((this.width / 2) - 70, 10, 20, 20, 0, 0, 20, SEARCH_ICON, 32, 64, (button) -> {
            this.blockEntryList.sortForSearch(this.searchField.getValue());
        }));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void tick() {
        this.searchField.tick();

        String searchString = this.searchField.getValue();

        if (!searchString.equalsIgnoreCase(this.lastSearch)) {
            this.blockEntryList.sortForSearch(searchString);
        }
        this.lastSearch = searchString;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.modName, this.width / 2, 5, -1);

        this.blockEntryList.render(matrixStack, mouseX, mouseX, partialTicks);
        this.searchField.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    /**
     * Opens the block config for the specified block.
     *
     * @param entry The block entry selected from the
     *              block entry list.
     */
    public void openBlockConfig(BlockListConfigScreen.BlockEntryList.BlockEntry entry) {
        this.minecraft.setScreen(new BlockConfigScreen(this, entry));
    }

    public class BlockEntryList extends ExtendedList<BlockListConfigScreen.BlockEntryList.BlockEntry> {

        public BlockEntryList(Minecraft minecraft) {
            super(minecraft, BlockListConfigScreen.this.width, BlockListConfigScreen.this.height, 40, BlockListConfigScreen.this.height - 45, 18);
            this.sortForSearch(BlockListConfigScreen.this.searchField.getValue());
        }

        /**
         * Looks for all blocks that have names that partially matches
         * the text in the search box and sorts the content alphabetically.
         */
        public void sortForSearch(String searchText) {
            this.clearEntries();
            this.setScrollAmount(0.0D);

            if (searchText.isEmpty()) {
                for (Block block : BlockListConfigScreen.this.modBlocks) {
                    this.addEntry(new BlockEntry(block, this.translateBlockName(block)));
                }
            }
            else {
                for (Block block : BlockListConfigScreen.this.modBlocks) {
                    String displayName = translateBlockName(block);

                    if (StringUtils.containsIgnoreCase(displayName, searchText)) {
                        this.addEntry(new BlockEntry(block, this.translateBlockName(block)));
                    }
                }
            }
            this.updateTextColor();
        }

        private void updateTextColor() {
                                                       // Red       Gray
            int textColor = this.children().isEmpty() ? 16733525 : 16777215;
            BlockListConfigScreen.this.searchField.setTextColor(textColor);
        }

        /** Returns the localized name of a block */
        private String translateBlockName(Block block) {
            ResourceLocation resourceLocation = block.getRegistryName();
            return I18n.get("block." + Objects.requireNonNull(resourceLocation).getNamespace() + "." + resourceLocation.getPath());
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
            private final String localizedName;
            private final Block block;
            private final ItemStack renderStack;

            public BlockEntry(Block block, String localizedName) {
                this.registryName = block.getRegistryName();
                this.localizedName = localizedName;
                this.block = block;

                this.renderStack = new ItemStack(block.asItem());

            }

            @Override
            public void render(MatrixStack matrixStack, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int mouseX, int mouseY, boolean isMouseOver, float p_230432_10_) {
                BlockListConfigScreen.this.font.drawShadow(matrixStack, this.localizedName, BlockEntryList.this.getRowLeft() + 24, (p_230432_3_ + 3), 16777215, true);

                Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(this.renderStack, BlockEntryList.this.getRowLeft(), (p_230432_3_ - 1));
            }

            public ResourceLocation getRegistryName() {
                return this.registryName;
            }

            public String getDisplayName() {
                return this.localizedName;
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
                BlockListConfigScreen.this.openBlockConfig(entry);
            }
        }
    }
}
