package mod.azure.hwg.item.weapons;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import mod.azure.hwg.HWGMod;
import mod.azure.hwg.client.ClientInit;
import mod.azure.hwg.client.render.weapons.MinigunRender;
import mod.azure.hwg.config.HWGConfig;
import mod.azure.hwg.entity.HWGEntity;
import mod.azure.hwg.entity.projectiles.BulletEntity;
import mod.azure.hwg.util.registry.HWGItems;
import mod.azure.hwg.util.registry.HWGSounds;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.SingletonGeoAnimatable;
import mod.azure.azurelib.animatable.client.RenderProvider;

public class Minigun extends AnimatedItem {

	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

	public Minigun() {
		super(new Item.Properties().stacksTo(1).durability(101));
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	@Override
	public void onUseTick(Level worldIn, LivingEntity entityLiving, ItemStack stack, int count) {
		if (entityLiving instanceof Player) {
			Player playerentity = (Player) entityLiving;
			if (stack.getDamageValue() < (stack.getMaxDamage() - 1)
					&& !playerentity.getCooldowns().isOnCooldown(this)) {
				playerentity.getCooldowns().addCooldown(this, 0);
				if (!worldIn.isClientSide) {
					BulletEntity abstractarrowentity = createArrow(worldIn, stack, playerentity);
					abstractarrowentity.shootFromRotation(playerentity, playerentity.getXRot(), playerentity.getYRot(), 0.0F,
							1.0F * 3.0F, 1.0F);
					abstractarrowentity.isNoGravity();

					stack.hurtAndBreak(1, entityLiving, p -> p.broadcastBreakEvent(entityLiving.getUsedItemHand()));
					worldIn.addFreshEntity(abstractarrowentity);
					worldIn.playSound((Player) null, playerentity.getX(), playerentity.getY(),
							playerentity.getZ(), HWGSounds.MINIGUN, SoundSource.PLAYERS, 0.25F, 1.0F);
					triggerAnim(playerentity, GeoItem.getOrAssignId(stack, (ServerLevel) worldIn), "shoot_controller",
							"firing");
				}
				boolean isInsideWaterBlock = playerentity.level.isWaterAt(playerentity.blockPosition());
				spawnLightSource(entityLiving, isInsideWaterBlock);
			}
		}
	}

	public static float getArrowVelocity(int charge) {
		float f = (float) charge / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack itemStack = user.getItemInHand(hand);
		user.startUsingItem(hand);
		return InteractionResultHolder.consume(itemStack);
	}

	public void reload(Player user, InteractionHand hand) {
		if (user.getItemInHand(hand).getItem() instanceof Minigun) {
			while (!user.isCreative() && user.getItemInHand(hand).getDamageValue() != 0
					&& user.getInventory().countItem(HWGItems.BULLETS) > 0) {
				removeAmmo(HWGItems.BULLETS, user);
				user.getItemInHand(hand).hurtAndBreak(-1, user, s -> user.broadcastBreakEvent(hand));
				user.getItemInHand(hand).setPopTime(3);
				user.getCommandSenderWorld().playSound((Player) null, user.getX(), user.getY(), user.getZ(),
						HWGSounds.CLIPRELOAD, SoundSource.PLAYERS, 1.00F, 1.0F);
			}
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if (world.isClientSide) {
			if (((Player) entity).getMainHandItem().getItem() instanceof Minigun && ClientInit.reload.isDown()
					&& selected) {
				FriendlyByteBuf passedData = new FriendlyByteBuf(Unpooled.buffer());
				passedData.writeBoolean(true);
				ClientPlayNetworking.send(HWGMod.MINIGUN, passedData);
			}
		}
		if (!(entity instanceof HWGEntity) && selected) {
			((LivingEntity) entity)
					.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1, 1, false, false, false));
		}
	}

	public BulletEntity createArrow(Level worldIn, ItemStack stack, LivingEntity shooter) {
		BulletEntity arrowentity = new BulletEntity(worldIn, shooter, HWGConfig.minigun_damage);
		return arrowentity;
	}

	public static float getPullProgress(int useTicks) {
		float f = (float) useTicks / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("hwg.ammo.reloadbullets").withStyle(ChatFormatting.ITALIC));
	}

	@Override
	public void createRenderer(Consumer<Object> consumer) {
		consumer.accept(new RenderProvider() {
			private final MinigunRender renderer = new MinigunRender();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return this.renderer;
			}
		});
	}

	@Override
	public Supplier<Object> getRenderProvider() {
		return this.renderProvider;
	}
}