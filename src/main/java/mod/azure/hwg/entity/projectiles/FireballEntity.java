package mod.azure.hwg.entity.projectiles;

import mod.azure.azurelib.common.internal.common.network.packet.EntityPacket;
import mod.azure.hwg.HWGMod;
import mod.azure.hwg.util.Helper;
import mod.azure.hwg.util.registry.HWGParticles;
import mod.azure.hwg.util.registry.HWGProjectiles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class FireballEntity extends AbstractArrow {

    public static final EntityDataAccessor<Float> FORCED_YAW = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private int idleTicks = 0;

    public FireballEntity(EntityType<? extends FireballEntity> entityType, Level world) {
        super(entityType, world, ItemStack.EMPTY);
        this.pickup = AbstractArrow.Pickup.DISALLOWED;
    }

    public FireballEntity(Level world, LivingEntity owner) {
        super(HWGProjectiles.FIREBALL, owner, world, ItemStack.EMPTY);
    }

    @Override
    public boolean displayFireAnimation() {
        return true;
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);
        if (HWGMod.config.gunconfigs.bullets_disable_iframes_on_players || !(living instanceof Player)) {
            living.invulnerableTime = 0;
            living.setDeltaMovement(0, 0, 0);
        }
    }

    @Override
    public void tickDespawn() {
        if (this.tickCount >= 40)
            this.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FORCED_YAW, 0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putShort("life", (short)this.tickCount);
        tag.putFloat("ForcedYaw", entityData.get(FORCED_YAW));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        this.tickCount = tag.getShort("life");
        entityData.set(FORCED_YAW, tag.getFloat("ForcedYaw"));
    }

    @Override
    public void tick() {
        var idleOpt = 100;
        if (getDeltaMovement().lengthSqr() < 0.01)
            idleTicks++;
        else
            idleTicks = 0;
        if (idleTicks < idleOpt)
            super.tick();
        if (this.tickCount >= 40)
            this.remove(Entity.RemovalReason.DISCARDED);
        if (getOwner() instanceof Player)
            setYRot(entityData.get(FORCED_YAW));
        var isInsideWaterBlock = level().isWaterAt(blockPosition());
        Helper.spawnLightSource(this, isInsideWaterBlock);
        if (this.level().isClientSide) {
            var x = this.getX() + (this.random.nextDouble() * 2.0D - 1.0D) * this.getBbWidth() * 0.5D;
            var y = this.getY() + 0.05D + this.random.nextDouble();
            var z = this.getZ() + (this.random.nextDouble() * 2.0D - 1.0D) * this.getBbWidth() * 0.5D;
            this.level().addParticle(ParticleTypes.FLAME, true, x, y, z, 0, 0, 0);
            this.level().addParticle(HWGParticles.BRIM_ORANGE, true, x, y, z, 0, 0, 0);
            this.level().addParticle(HWGParticles.BRIM_RED, true, x, y, z, 0, 0, 0);
        }
        this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2)).forEach(e -> {
            if (e.isAlive() && !(e instanceof Player) && !(this.getOwner() instanceof Player))
                e.setRemainingFireTicks(90);
        });
    }

    @Override
    public boolean isNoGravity() {
        return !this.isUnderWater();
    }

    @Override
    public void setSoundEvent(SoundEvent soundIn) {
        this.getDefaultHitGroundSoundEvent();
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.FIRE_AMBIENT;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (!this.level().isClientSide) {
            var entity = this.getOwner();
            if (!(entity instanceof Mob) || this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                var blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                if (this.level().isEmptyBlock(blockPos))
                    this.level().setBlockAndUpdate(blockPos, BaseFireBlock.getState(this.level(), blockPos));
            }
            this.remove(Entity.RemovalReason.DISCARDED);
        }
        this.setSoundEvent(SoundEvents.FIRE_AMBIENT);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        var entity = entityHitResult.getEntity();
        if (entityHitResult.getType() != HitResult.Type.ENTITY || !entityHitResult.getEntity().is(entity) && !this.level().isClientSide)
            this.remove(Entity.RemovalReason.DISCARDED);
        var entity2 = this.getOwner();
        DamageSource damageSource2;
        if (entity2 == null)
            damageSource2 = damageSources().arrow(this, this);
        else {
            damageSource2 = damageSources().arrow(this, entity2);
            if (entity2 instanceof LivingEntity livingEntity)
                livingEntity.setLastHurtMob(entity);
        }
        if (entity.hurt(damageSource2, HWGMod.config.gunconfigs.brimstoneconfigs.brimstone_damage)) {
            if (entity instanceof LivingEntity livingEntity) {
                if (!this.level().isClientSide && entity2 instanceof LivingEntity livingEntity1) {
                    EnchantmentHelper.doPostHurtEffects(livingEntity, entity2);
                    EnchantmentHelper.doPostDamageEffects(livingEntity1, livingEntity);
                    if (this.isOnFire())
                        livingEntity.setRemainingFireTicks(50);
                }
                this.doPostHurtEffects(livingEntity);
                if (livingEntity != entity2 && livingEntity instanceof Player && entity2 instanceof ServerPlayer serverPlayer && !this.isSilent())
                    serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
            }
        } else if (!this.level().isClientSide)
            this.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return Items.AIR.getDefaultInstance();
    }

}