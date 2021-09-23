package com.toast.blockproperties.client.config.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * The block config screen is where the user
 * can edit the properties of the chosen block.
 */
public class BlockConfigScreen extends Screen {

    private final Screen parent;

    protected BlockConfigScreen(Screen parent, BlockListConfigScreen.BlockEntryList.BlockEntry entry) {
        super(new StringTextComponent(entry.getDisplayName()));
        this.parent = parent;
    }


    @Override
    public void init() {

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
}
