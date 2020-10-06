package azmalent.potiondescriptions;

import azmalent.potiondescriptions.client.TooltipHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

@Mod(
        modid=PotionDescriptions.MODID,
        name=PotionDescriptions.NAME,
        version=PotionDescriptions.VERSION,
        dependencies="after:actuallyadditions;" +
                "after:bloodmagic;" +
                "after:botania;" +
                "after:rustic;" +
                "after:extraalchemy",
        clientSideOnly=true
)
public class PotionDescriptions
{
    public static final String MODID = "potiondescriptions";
    public static final String NAME = "Potion Descriptions";
    public static final String VERSION = "1.2";

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        MinecraftForge.EVENT_BUS.register(new TooltipHandler());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (ModConfig.loggingEnabled) {
            IForgeRegistry potionRegistry = GameRegistry.findRegistry(Potion.class);
            Collection<Potion> potions = potionRegistry.getValuesCollection();

            for (Potion potion : potions) {
                String translationKey = potion.getName() + ".desc";
                if (!I18n.hasKey(translationKey)) {
                    logger.warn(String.format(
                            "Missing description for effect '%s' (expected translation key: %s)",
                            I18n.format(potion.getName()), translationKey));
                }
            }
        }
    }
}
