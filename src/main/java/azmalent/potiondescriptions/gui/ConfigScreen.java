package azmalent.potiondescriptions.gui;

import azmalent.potiondescriptions.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    private static final int OPTIONS_LIST_TOP_HEIGHT = 24;
    private static final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
    private static final int OPTIONS_LIST_ITEM_HEIGHT = 25;

    private static final int DONE_BUTTON_WIDTH = 200;
    private static final int DONE_BUTTON_HEIGHT = 20;
    private static final int DONE_BUTTON_TOP_OFFSET = 26;

    private ButtonListWidget buttonList;

    public ConfigScreen(@Nullable Screen parent) {
        super(new TranslatableText("config.potiondescriptions.title"));
    }

    @Override
    protected void init() {
        buttonList = new ButtonListWidget(
            this.client, this.width, this.height,
            OPTIONS_LIST_TOP_HEIGHT,
            this.height - OPTIONS_LIST_BOTTOM_OFFSET,
            OPTIONS_LIST_ITEM_HEIGHT
        );

        buttonList.addSingleOptionEntry(new BooleanOption(
            "config.potiondescriptions.sneakingRequired",
            settings -> ConfigManager.config.sneakingRequired,
            (settings, value) -> ConfigManager.config.sneakingRequired = value
        ));

        buttonList.addSingleOptionEntry(new BooleanOption(
            "config.potiondescriptions.sneakingMessageEnabled",
            settings -> ConfigManager.config.sneakingMessageEnabled,
            (settings, value) -> ConfigManager.config.sneakingMessageEnabled = value
        ));

        buttonList.addSingleOptionEntry(new BooleanOption(
            "config.potiondescriptions.showSourceMod",
            settings -> ConfigManager.config.showSourceMod,
            (settings, value) -> ConfigManager.config.showSourceMod = value
        ));

        buttonList.addSingleOptionEntry(new BooleanOption(
            "config.potiondescriptions.showSuspiciousStewEffects",
            settings -> ConfigManager.config.showSuspiciousStewEffects,
            (settings, value) -> ConfigManager.config.showSuspiciousStewEffects = value
        ));

        this.children.add(this.buttonList);

        this.addButton(new ButtonWidget(
            (this.width - DONE_BUTTON_WIDTH) / 2,
            this.height - DONE_BUTTON_TOP_OFFSET,
                DONE_BUTTON_WIDTH, DONE_BUTTON_HEIGHT,
            new TranslatableText("gui.done"),
            button -> this.onClose()
        ));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.buttonList.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        ConfigManager.writeConfig();
        super.onClose();
    }
}
