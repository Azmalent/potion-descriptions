package azmalent.potiondescriptions.platform;

import azmalent.potiondescriptions.platform.services.IModIntegrationHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.brew.IBrewItem;

import java.util.List;

public class FabricModIntegrationHelper implements IModIntegrationHelper {
    @Override
    public boolean isBotaniaPotion(ItemStack itemStack) {
        return itemStack.getItem() instanceof IBrewItem;
    }

    @Override
    public List<MobEffectInstance> getBotaniaEffects(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return ((IBrewItem) item).getBrew(itemStack).getPotionEffects(itemStack);
    }

    //No reliquary on Fabric
    @Override
    public boolean isReliquaryPotion(ItemStack itemStack) {
        return false;
    }

    @Override
    public List<MobEffectInstance> getReliquaryEffects(ItemStack itemStack) {
        return null;
    }
}
