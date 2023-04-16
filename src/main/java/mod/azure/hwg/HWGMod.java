package mod.azure.hwg;

import eu.midnightdust.lib.config.MidnightConfig;
import mod.azure.azurelib.AzureLib;
import mod.azure.hwg.client.gui.GunTableScreenHandler;
import mod.azure.hwg.compat.BWCompat;
import mod.azure.hwg.compat.GigCompat;
import mod.azure.hwg.config.HWGConfig;
import mod.azure.hwg.network.PacketHandler;
import mod.azure.hwg.util.GunSmithProfession;
import mod.azure.hwg.util.MobAttributes;
import mod.azure.hwg.util.MobSpawn;
import mod.azure.hwg.util.recipes.GunTableRecipe;
import mod.azure.hwg.util.registry.HWGBlocks;
import mod.azure.hwg.util.registry.HWGItems;
import mod.azure.hwg.util.registry.HWGLoot;
import mod.azure.hwg.util.registry.HWGMobs;
import mod.azure.hwg.util.registry.HWGParticles;
import mod.azure.hwg.util.registry.HWGSounds;
import mod.azure.hwg.util.registry.ProjectilesEntityRegister;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class HWGMod implements ModInitializer {

	public static HWGMobs MOBS;
	public static HWGItems ITEMS;
	public static HWGBlocks BLOCKS;
	public static HWGSounds SOUNDS;
	public static BWCompat BW_ITEMS;
	public static GigCompat GIG_ITEMS;
	public static HWGParticles PARTICLES;
	public static final String MODID = "hwg";
	public static ProjectilesEntityRegister PROJECTILES;
	public static final ResourceLocation LUGER = new ResourceLocation(MODID, "luger");
	public static final ResourceLocation HELL = new ResourceLocation(MODID, "hellgun");
	public static final ResourceLocation ASSASULT = new ResourceLocation(MODID, "smg");
	public static final ResourceLocation BALROG = new ResourceLocation(MODID, "balrog");
	public static final ResourceLocation PISTOL = new ResourceLocation(MODID, "pistol");
	public static final ResourceLocation SNIPER = new ResourceLocation(MODID, "sniper");
	public static final ResourceLocation ASSASULT1 = new ResourceLocation(MODID, "smg1");
	public static final ResourceLocation ASSASULT2 = new ResourceLocation(MODID, "smg2");
	public static final ResourceLocation GPISTOL = new ResourceLocation(MODID, "gpistol");
	public static final ResourceLocation MEANIE1 = new ResourceLocation(MODID, "meanie1");
	public static final ResourceLocation MEANIE2 = new ResourceLocation(MODID, "meanie2");
	public static final ResourceLocation MINIGUN = new ResourceLocation(MODID, "minigun");
	public static final ResourceLocation SHOTGUN = new ResourceLocation(MODID, "shotgun");
	public static final ResourceLocation SPISTOL = new ResourceLocation(MODID, "spistol");
	public static final ResourceLocation GUNS = new ResourceLocation(MODID, "crafting_guns");
	public static final ResourceLocation BRIMSTONE = new ResourceLocation(MODID, "brimstone");
	public static final ResourceLocation SILVERGUN = new ResourceLocation(MODID, "silvergun");
	public static final ResourceLocation SILVERHELL = new ResourceLocation(MODID, "silverhell");
	public static final ResourceLocation FLAMETHOWER = new ResourceLocation(MODID, "flamethrower");
	public static final ResourceLocation GUNSMITH_POI = new ResourceLocation(MODID, "gun_smith_poi");
	public static final ResourceLocation GUN_TABLE_GUI = new ResourceLocation(MODID, "gun_table_gui");
	public static final ResourceLocation ROCKETLAUNCHER = new ResourceLocation(MODID, "rocketlauncher");
	public static MenuType<GunTableScreenHandler> SCREEN_HANDLER_TYPE;
	public static final CreativeModeTab WeaponItemGroup = FabricItemGroupBuilder.build(new ResourceLocation(MODID, "weapons"), () -> new ItemStack(HWGItems.AK47));
	public static final RecipeSerializer<GunTableRecipe> GUN_TABLE_RECIPE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(MODID, "gun_table"), new GunTableRecipe.Serializer());

	@Override
	public void onInitialize() {
		MidnightConfig.init(MODID, HWGConfig.class);
		ITEMS = new HWGItems();
		if (FabricLoader.getInstance().isModLoaded("gigeresque"))
			GIG_ITEMS = new GigCompat();
		if (FabricLoader.getInstance().isModLoaded("bewitchment"))
			BW_ITEMS = new BWCompat();
		BLOCKS = new HWGBlocks();
		SOUNDS = new HWGSounds();
		MOBS = new HWGMobs();
		PARTICLES = new HWGParticles();
		PROJECTILES = new ProjectilesEntityRegister();
		AzureLib.initialize();
		GunSmithProfession.init();
		MobSpawn.addSpawnEntries();
		MobAttributes.init();
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {
			if (HWGLoot.B_TREASURE.equals(id) || HWGLoot.JUNGLE.equals(id) || HWGLoot.U_BIG.equals(id) || HWGLoot.S_LIBRARY.equals(id) || HWGLoot.U_SMALL.equals(id) || HWGLoot.S_CORRIDOR.equals(id) || HWGLoot.S_CROSSING.equals(id) || HWGLoot.SPAWN_BONUS_CHEST.equals(id)) {
				LootPool poolBuilder = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).with(LootItem.lootTableItem(HWGItems.MEANIE1).build()).with(LootItem.lootTableItem(HWGItems.MEANIE2).build()).build();
				supplier.pool(poolBuilder);
			}
		});
		SCREEN_HANDLER_TYPE = new MenuType<>(GunTableScreenHandler::new);
		Registry.register(Registry.MENU, new ResourceLocation(MODID, "guntable_screen_type"), SCREEN_HANDLER_TYPE);
		PacketHandler.registerMessages();
	}
}