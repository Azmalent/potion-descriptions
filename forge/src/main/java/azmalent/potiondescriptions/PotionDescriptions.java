package azmalent.potiondescriptions;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.fml.config.ModConfig.Type;

@Mod(ModConstants.MODID)
public class PotionDescriptions
{
    public PotionDescriptions() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoadingContext.get().registerConfig(Type.CLIENT, ModConfig.SPEC);
            ModConfig.init(FMLPaths.CONFIGDIR.get().resolve(ModConstants.MODID + "-client.toml"));

            MinecraftForge.EVENT_BUS.addListener(PotionDescriptions::onTooltip);
        }
    }

    private static void onTooltip(ItemTooltipEvent event) {
        TooltipHandler.onTooltip(event.getItemStack(), event.getToolTip());
    }
}
