package mod.azure.hwg.item.weapons;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class HWGGunLoadedBase extends ProjectileWeaponItem {

    protected HWGGunLoadedBase(Properties settings) {
        super(settings);
    }

    public static float getArrowVelocity(int charge) {
        var f = charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F)
            f = 1.0F;

        return f;
    }

    public static float getPullProgress(int useTicks) {
        var f = useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F)
            f = 1.0F;

        return f;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("Ammo: " + (stack.getMaxDamage() - stack.getDamageValue() - 1) + " / " + (stack.getMaxDamage() - 1)).withStyle(ChatFormatting.ITALIC));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

}