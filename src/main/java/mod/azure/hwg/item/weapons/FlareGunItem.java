package mod.azure.hwg.item.weapons;

import com.google.common.collect.Lists;
import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.client.RenderProvider;
import mod.azure.azurelib.common.internal.common.animatable.SingletonGeoAnimatable;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.Animation;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.hwg.client.render.GunRender;
import mod.azure.hwg.entity.projectiles.BaseFlareEntity;
import mod.azure.hwg.item.enums.GunTypeEnum;
import mod.azure.hwg.util.registry.HWGItems;
import mod.azure.hwg.util.registry.HWGSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FlareGunItem extends HWGGunLoadedBase implements GeoItem {

    private boolean loaded = false;
    private boolean charged = false;
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    private static final Predicate<ItemStack> BLACK_FLARE = stack -> stack.getItem() == HWGItems.BLACK_FLARE;
    public static final Predicate<ItemStack> FLARE = BLACK_FLARE.or(stack -> stack.getItem() == HWGItems.BLUE_FLARE).or(stack -> stack.getItem() == HWGItems.BROWN_FLARE).or(stack -> stack.getItem() == HWGItems.CYAN_FLARE).or(stack -> stack.getItem() == HWGItems.GRAY_FLARE).or(stack -> stack.getItem() == HWGItems.GREEN_FLARE).or(stack -> stack.getItem() == HWGItems.LIGHTBLUE_FLARE).or(stack -> stack.getItem() == HWGItems.LIGHTGRAY_FLARE).or(stack -> stack.getItem() == HWGItems.LIME_FLARE).or(stack -> stack.getItem() == HWGItems.MAGENTA_FLARE).or(stack -> stack.getItem() == HWGItems.ORANGE_FLARE).or(stack -> stack.getItem() == HWGItems.PINK_FLARE).or(stack -> stack.getItem() == HWGItems.PURPLE_FLARE).or(stack -> stack.getItem() == HWGItems.RED_FLARE).or(stack -> stack.getItem() == HWGItems.WHITE_FLARE).or(stack -> stack.getItem() == HWGItems.YELLOW_FLARE);

    public FlareGunItem() {
        super(new Item.Properties().stacksTo(1).durability(31).component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY));
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    private void shootFlare(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack, List<ItemStack> projectile, boolean creative, float speed, float divergence, float simulated) {
        float i = 1.0F;
        for (ItemStack itemStack : projectile) {
            if (!itemStack.isEmpty()) {
                i = -i;
                if (!creative)
                    stack.hurtAndBreak(this.getDurabilityUse(itemStack), shooter, LivingEntity.getSlotForHand(hand));
                var flareEntity = new BaseFlareEntity(level, shooter, shooter.getX(), shooter.getEyeY() - 0.15000000596046448D, shooter.getZ(), true);
                var black = itemStack.getItem() == HWGItems.BLACK_FLARE;
                var blue = itemStack.getItem() == HWGItems.BLUE_FLARE;
                var brown = itemStack.getItem() == HWGItems.BROWN_FLARE;
                var cyan = itemStack.getItem() == HWGItems.CYAN_FLARE;
                var gray = itemStack.getItem() == HWGItems.GRAY_FLARE;
                var green = itemStack.getItem() == HWGItems.GREEN_FLARE;
                var lightBlue = itemStack.getItem() == HWGItems.LIGHTBLUE_FLARE;
                var lightGray = itemStack.getItem() == HWGItems.LIGHTGRAY_FLARE;
                var lime = itemStack.getItem() == HWGItems.LIME_FLARE;
                var magenta = itemStack.getItem() == HWGItems.MAGENTA_FLARE;
                var orange = itemStack.getItem() == HWGItems.ORANGE_FLARE;
                var pink = itemStack.getItem() == HWGItems.PINK_FLARE;
                var purple = itemStack.getItem() == HWGItems.PURPLE_FLARE;
                var red = itemStack.getItem() == HWGItems.RED_FLARE;
                var yellow = itemStack.getItem() == HWGItems.YELLOW_FLARE;
                if (black) flareEntity.setColor(1);
                else if (blue) flareEntity.setColor(2);
                else if (brown) flareEntity.setColor(3);
                else if (cyan) flareEntity.setColor(4);
                else if (gray) flareEntity.setColor(5);
                else if (green) flareEntity.setColor(6);
                else if (lightBlue) flareEntity.setColor(7);
                else if (lightGray) flareEntity.setColor(8);
                else if (lime) flareEntity.setColor(9);
                else if (magenta) flareEntity.setColor(10);
                else if (orange) flareEntity.setColor(11);
                else if (pink) flareEntity.setColor(12);
                else if (purple) flareEntity.setColor(13);
                else if (red) flareEntity.setColor(14);
                else if (yellow) flareEntity.setColor(15);
                else flareEntity.setColor(16);
                var vec3d = shooter.getUpVector(1.0F);
                var quaternionf = new Quaternionf().setAngleAxis((float) 0.0 * ((float) Math.PI / 180), vec3d.x, vec3d.y, vec3d.z);
                var vec3d2 = shooter.getViewVector(1.0f);
                var vector3f = vec3d2.toVector3f().rotate(quaternionf);
                vector3f.rotate(quaternionf);
                ((Projectile) flareEntity).shoot(vector3f.x, vector3f.y, vector3f.z, speed, divergence);
                flareEntity.setBaseDamage(0.3D);
                flareEntity.pickup = AbstractArrow.Pickup.DISALLOWED;
                if (!creative)
                    stack.hurtAndBreak(1, shooter, LivingEntity.getEquipmentSlotForItem(shooter.getUseItem()));
                level.addFreshEntity(flareEntity);
            }
        }
    }

    private static boolean tryLoadProjectiles(LivingEntity shooter, ItemStack crossbowStack) {
        List<ItemStack> list = draw(crossbowStack, shooter.getProjectile(crossbowStack), shooter);
        if (!list.isEmpty()) {
            crossbowStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(list));
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCharged(ItemStack stack) {
        ChargedProjectiles chargedProjectiles = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        return !chargedProjectiles.isEmpty();
    }

    public void shootAll(Level level, LivingEntity entity, InteractionHand hand, ItemStack stack, float speed, float divergence, @Nullable LivingEntity target) {
        if (!level.isClientSide()) {
            var chargedProjectiles = stack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
            if (chargedProjectiles != null && !chargedProjectiles.isEmpty()) {
                var bl = entity instanceof Player player && player.getAbilities().instabuild;
                this.shootFlare(level, entity, hand, stack, chargedProjectiles.getItems(), bl, speed, divergence, 0.0F);
            }
        }
    }

    public static int getPullTime(ItemStack stack) {
        var enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
        return enchantmentLevel == 0 ? 25 : 25 - 5 * enchantmentLevel;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", event -> PlayState.CONTINUE).triggerableAnim("firing", RawAnimation.begin().then("firing", Animation.LoopType.PLAY_ONCE)).triggerableAnim("loading", RawAnimation.begin().then("loading", Animation.LoopType.PLAY_ONCE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return Tiers.IRON.getRepairIngredient().test(ingredient) || super.isValidRepairItem(stack, ingredient);
    }

    @Override
    public @NotNull Predicate<ItemStack> getSupportedHeldProjectiles() {
        return FLARE;
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return FLARE;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 16;
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {

    }

    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var itemStack = player.getItemInHand(usedHand);
        ChargedProjectiles chargedProjectiles = itemStack.get(DataComponents.CHARGED_PROJECTILES);
        if (chargedProjectiles != null && !chargedProjectiles.isEmpty()) {
            shootAll(level, player, usedHand, itemStack, 2.6F, 1.0F, null);
            player.getCooldowns().addCooldown(this, 25);
            if (!level.isClientSide)
                triggerAnim(player, GeoItem.getOrAssignId(itemStack, (ServerLevel) level), "controller", "firing");
            return InteractionResultHolder.consume(itemStack);
        } else if (!player.getProjectile(itemStack).isEmpty()) {
            player.startUsingItem(usedHand);
            return InteractionResultHolder.consume(itemStack);
        } else {
            return InteractionResultHolder.fail(itemStack);
        }
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int remainingUseTicks) {
        if (!isCharged(stack) && tryLoadProjectiles(livingEntity, stack)) {
            var soundCategory = livingEntity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), HWGSounds.GLAUNCHERRELOAD, soundCategory, 0.5F,
                    1.0F);
            if (!level.isClientSide)
                triggerAnim(livingEntity, GeoItem.getOrAssignId(stack, (ServerLevel) level), "controller", "loading");
            if (livingEntity instanceof Player player)
                player.getCooldowns().addCooldown(this, 15);
        }
    }

    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClientSide) {
            var f = (float) (stack.getUseDuration() - remainingUseTicks) / (float) getPullTime(stack);
            if (f < 0.2F) {
                this.charged = false;
                this.loaded = false;
            }
            if (f >= 0.2F && !this.charged) this.charged = true;
            if (f >= 0.5F && !this.loaded) this.loaded = true;
        }
    }

    public int getUseDuration(ItemStack stack) {
        return getPullTime(stack) + 3000;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var chargedProjectiles = stack.get(DataComponents.CHARGED_PROJECTILES);
        if (chargedProjectiles != null && !chargedProjectiles.isEmpty()) {
            var itemStack = chargedProjectiles.getItems().getFirst();
            tooltipComponents.add(Component.literal("Ammo").append(CommonComponents.SPACE).append(itemStack.getDisplayName()));
            if (tooltipFlag.isAdvanced() && itemStack.getItem() == FLARE) {
                List<Component> list = Lists.newArrayList();
                HWGItems.FLARE_GUN.appendHoverText(itemStack, context, list, tooltipFlag);
                if (!list.isEmpty()) {
                    list.replaceAll(component -> Component.literal("  ").append(component).withStyle(
                            ChatFormatting.GRAY));
                    tooltipComponents.addAll(list);
                }
            }
        }
        tooltipComponents.add(Component.translatable("hwg.ammo.reloadflares").withStyle(ChatFormatting.ITALIC));
    }

    @Override
    public void createRenderer(Consumer<RenderProvider> consumer) {
        consumer.accept(new RenderProvider() {
            private final GunRender<FlareGunItem> renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new GunRender<FlareGunItem>("flare_gun", GunTypeEnum.FLARE);
            }
        });
    }

}
