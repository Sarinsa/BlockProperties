package com.toast.blockproperties.client.screen.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * The block config screen is where the user
 * can edit the properties of the chosen block.
 */
public class BlockConfigScreen extends Screen {

    private final Screen parent;

    private final Block block;
    private final ItemStack renderStack;


    protected BlockConfigScreen(Screen parent, BlockConfigEntry entry) {
        super(new StringTextComponent(entry.getDisplayName()));
        this.parent = parent;
        this.block = entry.getBlock();
        this.renderStack = entry.getRenderStack();
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 8, -1);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
