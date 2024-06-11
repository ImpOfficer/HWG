package mod.azure.hwg.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.Animation;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.hwg.HWGMod;
import mod.azure.hwg.entity.tasks.HWGMeleeAttackTask;
import mod.azure.hwg.entity.tasks.RangedShootingAttack;
import mod.azure.hwg.util.registry.HWGItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StrafeTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.UnreachableTargetSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MercEntity extends HWGEntity implements SmartBrainOwner<MercEntity> {

    public MercEntity(EntityType<MercEntity> entityType, Level worldIn) {
        super(entityType, worldIn);
        xpReward = HWGMod.config.mobconfigs.mercconfigs.merc_exp;
    }

    public static boolean canSpawn(EntityType<? extends HWGEntity> type, LevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return (world.getBrightness(LightLayer.BLOCK, pos) <= 8 || world.getDifficulty() == Difficulty.PEACEFUL) && checkAnyLightMonsterSpawnRules(type, world, spawnReason, pos, random);
    }

    public static AttributeSupplier.@NotNull Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 25.0D).add(Attributes.MOVEMENT_SPEED, 0.35D).add(Attributes.MAX_HEALTH, HWGMod.config.mobconfigs.mercconfigs.merc_health).add(Attributes.ARMOR, 3).add(Attributes.ATTACK_DAMAGE, 2D).add(Attributes.ARMOR_TOUGHNESS, 1D).add(Attributes.ATTACK_KNOCKBACK, 1.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "livingController", 0, event -> event.setAndContinue(
                RawAnimation.begin().thenLoop("idle"))));
        controllers.add(new AnimationController<>(this, "attackController", 0, event -> PlayState.STOP).triggerableAnim("ranged", RawAnimation.begin().then("attacking", Animation.LoopType.LOOP)).triggerableAnim("melee", RawAnimation.begin().then("melee", Animation.LoopType.PLAY_ONCE)).triggerableAnim("idle", RawAnimation.begin().thenWait(5).then("idle", Animation.LoopType.LOOP)));
    }

    @Override
    protected Brain.@NotNull Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }

    @Override
    public List<ExtendedSensor<MercEntity>> getSensors() {
        return ObjectArrayList.of(new NearbyPlayersSensor<>(), new NearbyLivingEntitySensor<MercEntity>().setPredicate((target, entity) -> target instanceof Player || target instanceof Villager), new HurtBySensor<>(), new UnreachableTargetSensor<>());
    }

    @Override
    public BrainActivityGroup<MercEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(new StrafeTarget<>(), new LookAtTarget<>(), new MoveToWalkTarget<>());
    }

    @Override
    public BrainActivityGroup<MercEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(new FirstApplicableBehaviour<MercEntity>(new TargetOrRetaliate<>(), new SetPlayerLookTarget<>().stopIf(target -> !target.isAlive() || (target instanceof Player player && !(player.isCreative() || player.isSpectator()))), new SetRandomLookTarget<>()), new OneRandomBehaviour<>(new SetRandomWalkTarget<>().speedModifier(1).startCondition(entity -> !entity.isAggressive()), new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))));
    }

    @Override
    public BrainActivityGroup<MercEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>().stopIf(target -> !target.isAlive() || (target instanceof Player player && (player.isCreative() || player.isSpectator()))),
                new RangedShootingAttack<>(5).whenStarting(entity -> setAggressive(true)).whenStarting(entity -> setAggressive(false)),
                new HWGMeleeAttackTask<>(3));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(tag.getInt("Variant"));
    }

    public int getVariant() {
        return Mth.clamp(entityData.get(VARIANT), 1, 4);
    }

    public void setVariant(int variant) {
        entityData.set(VARIANT, variant);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        final var biomeCheck = VillagerType.byBiome(level.getBiome(blockPosition()));
        setItemSlot(EquipmentSlot.MAINHAND, makeInitialWeapon());
        if ((biomeCheck == VillagerType.DESERT) || (biomeCheck == VillagerType.SAVANNA))
            setVariant(1);
        else if (biomeCheck == VillagerType.SWAMP)
            setVariant(2);
        else if (biomeCheck == VillagerType.JUNGLE)
            setVariant(2);
        else if (biomeCheck == VillagerType.SNOW)
            setVariant(3);
        else if (biomeCheck == VillagerType.TAIGA)
            setVariant(4);
        else if (biomeCheck == VillagerType.PLAINS)
            setVariant(4);
        else
            setVariant(getRandom().nextIntBetweenInclusive(0, 5));
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private ItemStack makeInitialWeapon() {
        final var givenList = Arrays.asList(HWGItems.PISTOL, HWGItems.LUGER, HWGItems.AK47, HWGItems.SHOTGUN, HWGItems.SMG);
        final var randomIndex = random.nextInt(givenList.size());
        final var randomElement = givenList.get(randomIndex);
        return new ItemStack(randomElement);
    }

    @Override
    public int getVariants() {
        return 4;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PILLAGER_HURT;
    }

}