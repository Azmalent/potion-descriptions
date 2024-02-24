package azmalent.potiondescriptions;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public class PotionDescriptions implements ClientModInitializer
{
    public static ConfigHolder<ModConfig> CONFIG;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ModConfig.class);

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> TooltipHandler.onTooltip(stack, lines));
    }
}
