package mod.azure.hwg.item.weapons;

import java.util.List;

import io.netty.buffer.Unpooled;
import mod.azure.hwg.HWGMod;
import mod.azure.hwg.client.ClientInit;
import mod.azure.hwg.entity.projectiles.FireballEntity;
import mod.azure.hwg.util.registry.HWGItems;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class BrimstoneItem extends HWGGunBase {
	public BrimstoneItem() {
		super(new Item.Properties().tab(HWGMod.WeaponItemGroup).stacksTo(1).durability(186));
	}

	@Override
	public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int remainingUseTicks) {
		if (entityLiving instanceof Player) {
			var playerentity = (Player) entityLiving;
			if (stack.getDamageValue() < (stack.getMaxDamage() - 6)) {
				playerentity.getCooldowns().addCooldown(this, 5);
				if (!worldIn.isClientSide) {
					var fireball = createArrow(worldIn, stack, playerentity);
					fireball.shootFromRotation(playerentity, playerentity.getXRot(), playerentity.getYRot(), 0.0F, 0.25F * 3.0F, 1.0F);
					fireball.moveTo(entityLiving.getX(), entityLiving.getY(0.5), entityLiving.getZ(), 0, 0);
					stack.hurtAndBreak(1, entityLiving, p -> p.broadcastBreakEvent(entityLiving.getUsedItemHand()));
					fireball.setRemainingFireTicks(100);
					fireball.setKnockback(1);

					var fireball1 = createArrow(worldIn, stack, playerentity);
					fireball1.shootFromRotation(playerentity, playerentity.getXRot(), playerentity.getYRot() + 5, 0.0F, 0.25F * 3.0F, 1.0F);
					fireball1.moveTo(entityLiving.getX(), entityLiving.getY(0.5), entityLiving.getZ(), 0, 0);
					stack.hurtAndBreak(1, entityLiving, p -> p.broadcastBreakEvent(entityLiving.getUsedItemHand()));
					fireball1.setRemainingFireTicks(100);
					fireball1.setKnockback(1);

					var fireball2 = createArrow(worldIn, stack, playerentity);
					fireball2.shootFromRotation(playerentity, playerentity.getXRot(), playerentity.getYRot() - 5, 0.0F, 0.25F * 3.0F, 1.0F);
					fireball2.moveTo(entityLiving.getX(), entityLiving.getY(0.5), entityLiving.getZ(), 0, 0);
					stack.hurtAndBreak(1, entityLiving, p -> p.broadcastBreakEvent(entityLiving.getUsedItemHand()));
					fireball2.setRemainingFireTicks(100);
					fireball2.setKnockback(1);

					worldIn.addFreshEntity(fireball);
					worldIn.addFreshEntity(fireball1);
					worldIn.addFreshEntity(fireball2);

					stack.hurtAndBreak(6, entityLiving, p -> p.broadcastBreakEvent(entityLiving.getUsedItemHand()));

					worldIn.playSound((Player) null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.FIREWORK_ROCKET_BLAST_FAR, SoundSource.PLAYERS, 1.0F, 1.0F / (worldIn.random.nextFloat() * 0.4F + 1.2F) + 1F * 0.5F);
				}
				var isInsideWaterBlock = playerentity.level.isWaterAt(playerentity.blockPosition());
				spawnLightSource(entityLiving, isInsideWaterBlock);
			}
		}
	}

	public FireballEntity createArrow(Level worldIn, ItemStack stack, LivingEntity shooter) {
		var fireball = new FireballEntity(worldIn, shooter);
		return fireball;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if (world.isClientSide)
			if (((Player) entity).getMainHandItem().getItem() instanceof BrimstoneItem) {
				if (ClientInit.reload.isDown() && selected) {
					FriendlyByteBuf passedData = new FriendlyByteBuf(Unpooled.buffer());
					passedData.writeBoolean(true);
					ClientPlayNetworking.send(HWGMod.BRIMSTONE, passedData);
				}
			}
	}

	public void reload(Player user, InteractionHand hand) {
		if (user.getItemInHand(hand).getItem() instanceof BrimstoneItem) {
			while (!user.isCreative() && user.getItemInHand(hand).getDamageValue() != 0 && user.getInventory().countItem(HWGItems.FUEL_TANK) > 0) {
				removeAmmo(HWGItems.FUEL_TANK, user);
				user.getItemInHand(hand).hurtAndBreak(-186, user, s -> user.broadcastBreakEvent(hand));
				user.getItemInHand(hand).setPopTime(3);
				user.getCommandSenderWorld().playSound((Player) null, user.getX(), user.getY(), user.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 1.5F);
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
		tooltip.add(Component.translatable("Fuel: " + (stack.getMaxDamage() - stack.getDamageValue() - 6) + " / " + (stack.getMaxDamage() - 6)).withStyle(ChatFormatting.ITALIC));
		tooltip.add(Component.translatable("hwg.ammo.reloadfuel").withStyle(ChatFormatting.ITALIC));
	}
}