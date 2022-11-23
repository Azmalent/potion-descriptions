package azmalent.potiondescriptions;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.fml.config.ModConfig.Type;

@Mod(PotionDescriptions.MODID)
public class PotionDescriptions
{
    public static final String MODID = "potiondescriptions";
    public static Logger LOGGER = LogManager.getLogger(MODID);

    public PotionDescriptions() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoadingContext.get().registerConfig(Type.CLIENT, ModConfig.SPEC);
            ModConfig.init(FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml"));

            MinecraftForge.EVENT_BUS.addListener(TooltipHandler::onTooltipDisplayed);
        }
    }
}
