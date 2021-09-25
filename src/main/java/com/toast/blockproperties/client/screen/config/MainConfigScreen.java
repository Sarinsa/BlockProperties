package com.toast.blockproperties.client.screen.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.toast.blockproperties.common.misc.TranslationStrings;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.text.Collator;
import java.util.*;

/**
 * The main config screen for Block Properties.
 * Displays a list of all installed mods that
 * adds blocks to the game.
 */
public class MainConfigScreen extends Screen {

    /** The previous screen, usually the Forge main config screen. */
    private final Screen parent;

    /** A text field for searching for a specific mod*/
    private TextFieldWidget searchField;

    /**
     * The last search String that was typed into
     * the search text field. Stored for GUI
     * re-initialization when the game window
     * is resized and whatnot.
     */
    private String lastSearch = "";

    /** A map of modids and their respective display name. */
    private final Map<String, String> modEntries = new TreeMap<>(Collator.getInstance());

    /** A scrollable list showing installed mods. */
    private ModEntryList modList;


    public MainConfigScreen(Minecraft minecraft, Screen parent) {
        super(new TranslationTextComponent(TranslationStrings.MAIN_SCREEN_TITLE));
        this.parent = parent;

        List<String> modids = new ArrayList<>();

        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            String namespace = block.getRegistryName().getNamespace();

            if (!modids.contains(namespace)) {
                modids.add(namespace);
            }
        }

        for(ModInfo modInfo : ModList.get().getMods()) {
            if (modids.contains(modInfo.getModId())) {
                this.modEntries.put(modInfo.getDisplayName(), modInfo.getModId());
            }
        }
    }

    @Override
    public void init() {
        this.searchField = new TextFieldWidget(this.minecraft.font, (this.width / 2) - 40, 18, 80, 14, new TranslationTextComponent("itemGroup.search"));
        this.searchField.setMaxLength(50);
        this.searchField.setBordered(true);
        this.searchField.setVisible(true);
        this.searchField.setTextColor(16777215);
        this.searchField.setValue(this.lastSearch.isEmpty() ? "" : this.lastSearch);
        this.children.add(this.searchField);

        this.modList = new ModEntryList(this.minecraft);
        this.children.add(this.modList);
        this.modList.sortForSearch(this.lastSearch);

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
    public void tick() {
        this.searchField.tick();

        String searchString = this.searchField.getValue();

        if (!searchString.equalsIgnoreCase(this.lastSearch)) {
            this.modList.sortForSearch(searchString);
        }
        this.lastSearch = searchString;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.modList.render(matrixStack, mouseX, mouseY, partialTicks);
        this.searchField.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public class ModEntryList extends ExtendedList<MainConfigScreen.ModEntryList.ModEntry> {

        public ModEntryList(Minecraft minecraft) {
            super(minecraft, MainConfigScreen.this.width, MainConfigScreen.this.height, 45, MainConfigScreen.this.height - 43, 18);

            MainConfigScreen.this.modEntries.forEach((String displayName, String modid) -> {
                this.addEntry(new ModEntry(displayName, modid));
            });
        }

        /**
         * Looks for all mod names that partially or exactly matches
         * the text in the search box and sorts the content.
         */
        public void sortForSearch(String searchText) {
            this.clearEntries();
            this.setScrollAmount(0.0D);

            if (searchText.isEmpty()) {
                MainConfigScreen.this.modEntries.forEach((String displayName, String modid) -> {
                    this.addEntry(new ModEntry(displayName, modid));
                });
            }
            else {
                MainConfigScreen.this.modEntries.forEach((String displayName, String modid) -> {

                    if (StringUtils.containsIgnoreCase(displayName, searchText)) {
                        this.addEntry(new ModEntry(displayName, modid));
                    }
                });
            }
            this.updateTextColor();
        }

        private void updateTextColor() {
            // Red       Gray
            int textColor = this.children().isEmpty() ? 16733525 : 16777215;
            MainConfigScreen.this.searchField.setTextColor(textColor);
        }

        /**
         * Opens the block list config screen for the
         * specified mod entry.
         *
         * @param entry The mod entry selected from
         *              the mod entry list.
         */
        public void openCategory(ModEntry entry) {
            MainConfigScreen.this.minecraft.setScreen(new BlockListConfigScreen(MainConfigScreen.this, entry));
            this.minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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
        public void setSelected(@Nullable MainConfigScreen.ModEntryList.ModEntry modEntry) {
            super.setSelected(modEntry);
        }

        @Override
        protected void renderBackground(MatrixStack matrixStack) {
            MainConfigScreen.this.renderBackground(matrixStack);
        }

        @Override
        protected boolean isFocused() {
            return MainConfigScreen.this.getFocused() == this;
        }


        public class ModEntry extends ExtendedList.AbstractListEntry<ModEntryList.ModEntry> {

            private final String modName;
            private final String modid;

            public ModEntry(String modName, String modid) {
                this.modName = modName;
                this.modid = modid;
            }

            @Override
            public void render(MatrixStack matrixStack, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int mouseX, int mouseY, boolean isMouseOver, float p_230432_10_) {
                int color = isMouseOver ? 16777045 : 16777215;
                MainConfigScreen.this.font.drawShadow(matrixStack, this.modName, (float)(MainConfigScreen.ModEntryList.this.width / 2 - MainConfigScreen.this.font.width(this.modName) / 2), (float)(p_230432_3_ + 1), color, true);
            }

            public String getModid() {
                return this.modid;
            }

            public String getModName() {
                return this.modName;
            }

            @Override                  // Parameter mappings would be nice
            public boolean mouseClicked(double what, double are, int these) {
                if (these == 0) {
                    this.openCategory();
                    return true;
                }
                else {
                    return false;
                }
            }

            private void openCategory() {
                MainConfigScreen.ModEntryList.this.openCategory(this);
            }
        }
    }
}
