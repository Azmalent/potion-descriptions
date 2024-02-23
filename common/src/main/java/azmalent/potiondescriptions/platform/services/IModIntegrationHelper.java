package azmalent.potiondescriptions.platform.services;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IModIntegrationHelper {
    boolean isBotaniaPotion(ItemStack itemStack);
    List<MobEffectInstance> getBotaniaEffects(ItemStack itemStack);

    boolean isReliquaryPotion(ItemStack itemStack);
    List<MobEffectInstance> getReliquaryEffects(ItemStack itemStack);
}
