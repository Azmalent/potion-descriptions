package azmalent.potiondescriptions.compat.botania;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IBotaniaCompat {
    boolean isBrewItem(Item item);
    List<StatusEffectInstance> getBrewEffects(ItemStack stack);
}
