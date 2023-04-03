package mod.azure.hwg.item.weapons;

import java.util.List;

import mod.azure.azurelib.AzureLibMod;
import mod.azure.azurelib.entities.TickingLightEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class HWGGunBase extends Item {

	private BlockPos lightBlockPos = null;

	public HWGGunBase(Properties settings) {
		super(settings);
	}

	public void removeAmmo(Item ammo, Player playerEntity) {
		if (!playerEntity.isCreative()) {
			for (ItemStack item : playerEntity.getInventory().offhand) {
				if (item.getItem() == ammo) {
					item.shrink(1);
					break;
				}
				for (ItemStack item1 : playerEntity.getInventory().items) {
					if (item1.getItem() == ammo) {
						item1.shrink(1);
						break;
					}
				}
			}
		}
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
	public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
		return super.isValidRepairItem(stack, ingredient);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
		tooltip.add(Component.translatable("Ammo: " + (stack.getMaxDamage() - stack.getDamageValue() - 1) + " / " + (stack.getMaxDamage() - 1)).withStyle(ChatFormatting.ITALIC));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		var itemStack = user.getItemInHand(hand);
		user.startUsingItem(hand);
		return InteractionResultHolder.consume(itemStack);
	}

	protected void spawnLightSource(Entity entity, boolean isInWaterBlock) {
		if (lightBlockPos == null) {
			lightBlockPos = findFreeSpace(entity.level, entity.blockPosition(), 2);
			if (lightBlockPos == null)
				return;
			entity.level.setBlockAndUpdate(lightBlockPos, AzureLibMod.TICKING_LIGHT_BLOCK.defaultBlockState());
		} else if (checkDistance(lightBlockPos, entity.blockPosition(), 2)) {
			var blockEntity = entity.level.getBlockEntity(lightBlockPos);
			if (blockEntity instanceof TickingLightEntity)
				((TickingLightEntity) blockEntity).refresh(isInWaterBlock ? 20 : 0);
			else
				lightBlockPos = null;
		} else
			lightBlockPos = null;
	}

	private boolean checkDistance(BlockPos blockPosA, BlockPos blockPosB, int distance) {
		return Math.abs(blockPosA.getX() - blockPosB.getX()) <= distance && Math.abs(blockPosA.getY() - blockPosB.getY()) <= distance && Math.abs(blockPosA.getZ() - blockPosB.getZ()) <= distance;
	}

	private BlockPos findFreeSpace(Level world, BlockPos blockPos, int maxDistance) {
		if (blockPos == null)
			return null;

		var offsets = new int[maxDistance * 2 + 1];
		offsets[0] = 0;
		for (int i = 2; i <= maxDistance * 2; i += 2) {
			offsets[i - 1] = i / 2;
			offsets[i] = -i / 2;
		}
		for (int x : offsets)
			for (int y : offsets)
				for (int z : offsets) {
					BlockPos offsetPos = blockPos.offset(x, y, z);
					BlockState state = world.getBlockState(offsetPos);
					if (state.isAir() || state.getBlock().equals(AzureLibMod.TICKING_LIGHT_BLOCK))
						return offsetPos;
				}

		return null;
	}

	public static float getPowerForTime(int charge) {
		var f = (float) charge / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		if (f > 1.0F)
			f = 1.0F;

		return f;
	}

	public static EntityHitResult hitscanTrace(Player player, double range, float ticks) {
		var look = player.getViewVector(ticks);
		var start = player.getEyePosition(ticks);
		var end = new Vec3(player.getX() + look.x * range, player.getEyeY() + look.y * range, player.getZ() + look.z * range);
		var traceDistance = player.level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getLocation().distanceToSqr(end);
		for (var possible : player.level.getEntities(player, player.getBoundingBox().expandTowards(look.scale(traceDistance)).expandTowards(3.0D, 3.0D, 3.0D), (entity -> !entity.isSpectator() && entity.isPickable() && entity instanceof LivingEntity))) {
			if (possible.getBoundingBox().inflate(0.3D).clip(start, end).isPresent())
				if (start.distanceToSqr(possible.getBoundingBox().inflate(0.3D).clip(start, end).get()) < traceDistance)
					return ProjectileUtil.getEntityHitResult(player.level, player, start, end, player.getBoundingBox().expandTowards(look.scale(traceDistance)).inflate(3.0D, 3.0D, 3.0D), (target) -> !target.isSpectator() && player.isAttackable() && player.hasLineOfSight(target));
		}
		return null;
	}

}