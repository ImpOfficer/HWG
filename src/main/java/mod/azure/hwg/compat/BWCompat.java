package mod.azure.hwg.compat;

import java.util.LinkedList;
import java.util.List;

import mod.azure.hwg.HWGMod;
import mod.azure.hwg.entity.projectiles.SBulletEntity;
import mod.azure.hwg.item.weapons.SilverGunItem;
import mod.azure.hwg.item.weapons.SilverRevolverItem;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;

public class BWCompat {

	public static List<EntityType<? extends Entity>> ENTITY_TYPES = new LinkedList();
	public static List<EntityType<? extends Entity>> ENTITY_THAT_USE_ITEM_RENDERS = new LinkedList();

	public static SilverGunItem SILVERGUN = item(new SilverGunItem(), "silvergun");
	public static Item SILVERBULLET = item(new Item(new Item.Properties()), "silver_bullet");
	public static SilverRevolverItem SILVERHELLHORSE = item(new SilverRevolverItem(), "shellhorse_revolver");
	public static EntityType<SBulletEntity> SILVERBULLETS = projectile(SBulletEntity::new, "silverbullets");

	static <T extends Item> T item(T c, String id) {
		Registry.register(Registry.ITEM, new ResourceLocation(HWGMod.MODID, id), c);
		return c;
	}

	private static <T extends Entity> EntityType<T> projectile(EntityType.EntityFactory<T> factory, String id) {
		return projectile(factory, id, true);
	}

	private static <T extends Entity> EntityType<T> projectile(EntityType.EntityFactory<T> factory, String id, boolean itemRender) {

		EntityType<T> type = FabricEntityTypeBuilder.<T>create(MobCategory.MISC, factory).dimensions(new EntityDimensions(0.5F, 0.5F, true)).disableSummon().spawnableFarFromPlayer().trackRangeBlocks(90).trackedUpdateRate(1).build();

		Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(HWGMod.MODID, id), type);

		ENTITY_TYPES.add(type);

		if (itemRender)
			ENTITY_THAT_USE_ITEM_RENDERS.add(type);

		return type;
	}
}
