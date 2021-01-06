package azmalent.potiondescriptions.mixin;

import azmalent.potiondescriptions.ConfigManager;
import azmalent.potiondescriptions.PotionDescriptions;
import azmalent.potiondescriptions.util.TooltipUtil;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.brew.IBrewItem;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    private static boolean BOTANIA_LOADED = FabricLoader.getInstance().isModLoaded("botania");

    private static List<StatusEffectInstance> getSuspiciousStewEffects(ItemStack stew) {
        List<StatusEffectInstance> effectInstances = new ArrayList<>();

        CompoundTag tag = stew.getTag();
        if (tag != null && tag.contains("Effects", NbtType.LIST)) {
            ListTag list = tag.getList("Effects", NbtType.COMPOUND);
            for(int i = 0; i < list.size(); i++) {
                CompoundTag effect = list.getCompound(i);
                byte id = effect.getByte("EffectId");
                int duration = effect.contains("EffectDuration", NbtType.INT) ? effect.getInt("EffectDuration") : 160;

                if (StatusEffect.byRawId(id) != null) {
                    effectInstances.add(new StatusEffectInstance(StatusEffect.byRawId(id), duration));
                }
            }
        }

        return effectInstances;
    }

    @Inject(at = @At("TAIL"), method = "getTooltip(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;")
    public void appendTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.isEmpty()) return;

        Item item = stack.getItem();

        if (stack.hasTag() && stack.getTag().contains("Potion")) {
            List<StatusEffectInstance> effects = PotionUtil.getPotionEffects(stack);
            TooltipUtil.addDescriptionTooltip(effects, cir.getReturnValue());
        }
        else if (item == Items.SUSPICIOUS_STEW && ConfigManager.config.showSuspiciousStewEffects) {
            List<StatusEffectInstance> effects = getSuspiciousStewEffects(stack);
            TooltipUtil.addEffectTooltip(effects, cir.getReturnValue());
            TooltipUtil.addDescriptionTooltip(effects, cir.getReturnValue());
        }
        else if (BOTANIA_LOADED && PotionDescriptions.BOTANIA_COMPAT.isBrewItem(item)) {
            List<StatusEffectInstance> effects = PotionDescriptions.BOTANIA_COMPAT.getBrewEffects(stack);
            TooltipUtil.addDescriptionTooltip(effects, cir.getReturnValue());
        }
    }
}
