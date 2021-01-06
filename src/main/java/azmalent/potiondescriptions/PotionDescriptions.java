package azmalent.potiondescriptions;

import azmalent.potiondescriptions.compat.botania.BotaniaCompatDummy;
import azmalent.potiondescriptions.compat.botania.IBotaniaCompat;
import azmalent.potiondescriptions.mixin.ItemStackMixin;
import azmalent.potiondescriptions.util.TooltipUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public class PotionDescriptions implements ClientModInitializer {
    public static final String MODID = "potiondescriptions";

    public static IBotaniaCompat BOTANIA_COMPAT;

    static {
        try {
            BOTANIA_COMPAT = FabricLoader.getInstance().isModLoaded("botania")
                ? Class.forName("azmalent.potiondescriptions.compat.botania.BotaniaCompatImpl").asSubclass(IBotaniaCompat.class).newInstance()
                : new BotaniaCompatDummy();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitializeClient() {
        ConfigManager.init();
    }
}
