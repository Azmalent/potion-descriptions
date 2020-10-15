package azmalent.potiondescriptions;

import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Effect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

import static net.minecraftforge.fml.config.ModConfig.Type;

@Mod(PotionDescriptions.MODID)
public class PotionDescriptions
{
    public static final String MODID = "potiondescriptions";
    public static Logger log = LogManager.getLogger(MODID);

    public PotionDescriptions() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoadingContext.get().registerConfig(Type.CLIENT, ModConfig.SPEC);
            ModConfig.init(FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));

            FMLJavaModLoadingContext.get().getModEventBus().addListener(PotionDescriptions::onLoadComplete);
            MinecraftForge.EVENT_BUS.addListener(TooltipHandler::onTooltipDisplayed);
        }
    }

    @SubscribeEvent
    public static void onLoadComplete(final FMLLoadCompleteEvent event) {
        if (ModConfig.loggingEnabled.get()) {
            Collection<Effect> effects = GameRegistry.findRegistry(Effect.class).getValues();
            for (Effect effect : effects) {
                String translationKey = "description." + effect.getName();
                if (!I18n.hasKey(translationKey)) {
                    log.warn(String.format(
                            "Missing description for effect '%s' (expected translation key: %s)",
                            I18n.format(effect.getName()), translationKey));
                }
            }
        }
    }
}
