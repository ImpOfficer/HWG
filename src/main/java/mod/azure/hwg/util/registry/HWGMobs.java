package mod.azure.hwg.util.registry;

import mod.azure.hwg.HWGMod;
import mod.azure.hwg.entity.MercEntity;
import mod.azure.hwg.entity.SpyEntity;
import mod.azure.hwg.entity.TechnodemonEntity;
import mod.azure.hwg.entity.TechnodemonGreaterEntity;
import mod.azure.hwg.entity.blockentity.GunBlockEntity;
import mod.azure.hwg.entity.projectiles.FuelTankEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;

public record HWGMobs() {

    public static final EntityType<TechnodemonEntity> TECHNOLESSER = Registry.register(BuiltInRegistries.ENTITY_TYPE, HWGMod.modResource("technodemon_lesser_1"), EntityType.Builder.of(TechnodemonEntity::new, MobCategory.MONSTER).sized(0.9f, 2.5F).fireImmune().clientTrackingRange(90).updateInterval(1).build());
    public static final EntityType<TechnodemonGreaterEntity> TECHNOGREATER = Registry.register(BuiltInRegistries.ENTITY_TYPE, HWGMod.modResource("technodemon_greater_1"), EntityType.Builder.of(TechnodemonGreaterEntity::new, MobCategory.MONSTER).sized(1.3f, 3.5F).fireImmune().clientTrackingRange(90).updateInterval(1).build());
    public static final EntityType<MercEntity> MERC = Registry.register(BuiltInRegistries.ENTITY_TYPE, HWGMod.modResource("merc"), EntityType.Builder.of(MercEntity::new, MobCategory.MONSTER).sized(1.1F, 2.1F).clientTrackingRange(90).updateInterval(1).build());
    public static final EntityType<SpyEntity> SPY = Registry.register(BuiltInRegistries.ENTITY_TYPE, HWGMod.modResource("spy"), EntityType.Builder.of(SpyEntity::new, MobCategory.MONSTER).sized(1.1F, 2.1F).clientTrackingRange(90).updateInterval(1).build());
    public static final EntityType<FuelTankEntity> FUELTANK = Registry.register(BuiltInRegistries.ENTITY_TYPE, HWGMod.modResource("fuel_tank"), EntityType.Builder.<FuelTankEntity>of(FuelTankEntity::new, MobCategory.MISC).sized(0.98F, 0.98F).clientTrackingRange(90).updateInterval(1).build());
    public static final BlockEntityType<GunBlockEntity> GUN_TABLE_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, HWGMod.MODID + ":guntable", BlockEntityType.Builder.of(GunBlockEntity::new, HWGBlocks.GUN_TABLE).build(null));

    public static void initialize() {
    }
}
