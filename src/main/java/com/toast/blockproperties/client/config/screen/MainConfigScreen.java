package com.toast.blockproperties.client.config.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.toast.blockproperties.common.misc.TranslationStrings;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MainConfigScreen extends Screen {

    /** The previous screen, usually the Forge main config screen. */
    private final Screen parent;

    /** A scrollable list of installed mods. */
    private ModEntryList modList;


    public MainConfigScreen(Minecraft minecraft, Screen parent) {
        super(new TranslationTextComponent(TranslationStrings.MAIN_SCREEN_TITLE));
        this.parent = parent;
    }

    @Override
    public void init() {
        this.modList = new ModEntryList(this.minecraft);
        this.children.add(this.modList);

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
        this.modList.render(matrixStack, mouseX, mouseX, partialTicks);

        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 10, -1);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public class ModEntryList extends ExtendedList<MainConfigScreen.ModEntryList.ModEntry> {

        public ModEntryList(Minecraft minecraft) {
            super(minecraft, MainConfigScreen.this.width, MainConfigScreen.this.height, 40, MainConfigScreen.this.height - 45, 18);

            List<String> includedMods = new ArrayList<>();

            for (Block block : ForgeRegistries.BLOCKS.getValues()) {
                String namespace = block.getRegistryName().getNamespace();

                if (!includedMods.contains(namespace))
                    includedMods.add(namespace);
            }

            for(ModInfo modInfo : ModList.get().getMods()) {
                if (includedMods.contains(modInfo.getModId())) {
                    MainConfigScreen.ModEntryList.ModEntry modEntry = new MainConfigScreen.ModEntryList.ModEntry(modInfo.getDisplayName(), modInfo.getModId());
                    this.addEntry(modEntry);
                }
            }
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
            public void render(MatrixStack matrixStack, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
                String s = this.modName;
                MainConfigScreen.this.font.drawShadow(matrixStack, s, (float)(MainConfigScreen.ModEntryList.this.width / 2 - MainConfigScreen.this.font.width(s) / 2), (float)(p_230432_3_ + 1), 16777215, true);
            }

            public String getModid() {
                return this.modid;
            }

            public String getModName() {
                return this.modName;
            }

            @Override
            public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
                if (p_231044_5_ == 0) {
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
