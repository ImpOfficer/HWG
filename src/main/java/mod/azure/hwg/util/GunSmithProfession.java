package mod.azure.hwg.util;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mod.azure.hwg.HWGMod;
import mod.azure.hwg.mixin.PointOfInterestTypesInvoker;
import mod.azure.hwg.util.registry.HWGBlocks;
import mod.azure.hwg.util.registry.HWGItems;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradeOffers.Factory;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class GunSmithProfession {

	public static final Supplier<PointOfInterestType> GUNSMITH_POI = registerPoiType("gun_smith",
			() -> new PointOfInterestType(PointOfInterestTypesInvoker.invokeGetBlockStates(HWGBlocks.GUN_TABLE), 1, 1));
	public static final Supplier<VillagerProfession> GUNSMITH = registerProfession("gun_smith",
			() -> new VillagerProfession("gun_smith", holder -> holder.value().equals(GUNSMITH_POI.get()),
					holder -> holder.value().equals(GUNSMITH_POI.get()), ImmutableSet.of(), ImmutableSet.of(),
					SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM));

	public static Supplier<VillagerProfession> registerProfession(String name,
			Supplier<VillagerProfession> profession) {
		var registry = Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(HWGMod.MODID, name),
				profession.get());
		return () -> registry;
	}

	public static Supplier<PointOfInterestType> registerPoiType(String name, Supplier<PointOfInterestType> poiType) {
		RegistryKey<PointOfInterestType> resourceKey = RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY,
				new Identifier(HWGMod.MODID, name));
		var registry = Registry.register(Registry.POINT_OF_INTEREST_TYPE, resourceKey, poiType.get());
		PointOfInterestTypesInvoker.invokeRegisterBlockStates(Registry.POINT_OF_INTEREST_TYPE.getHolderOrThrow(resourceKey));
		return () -> registry;
	}

	public static void init() {
		TradeOffers.PROFESSION_TO_LEVELED_TRADE.put(GUNSMITH.get(), copyToFastUtilMap(ImmutableMap.of(1,
				new TradeOffers.Factory[] { new GunSmithProfession.BuyForOneEmeraldFactory(Items.GUNPOWDER, 1, 16, 2),
						new GunSmithProfession.SellItemFactory(Items.IRON_NUGGET, 2, 1, 16, 1) },
				2,
				new TradeOffers.Factory[] {
						new GunSmithProfession.BuyForItemFactory(Items.EMERALD, HWGItems.BULLETS, 2, 16, 10),
						new GunSmithProfession.BuyForItemFactory(Items.EMERALD, HWGItems.PISTOL, 5, 16, 20),
						new GunSmithProfession.BuyForItemFactory(Items.EMERALD, HWGItems.LUGER, 5, 16, 20) },
				3,
				new TradeOffers.Factory[] {
						new GunSmithProfession.BuyForItemsFactory(Items.EMERALD, 2, 1, HWGItems.SHOTGUN_SHELL, 16, 16,
								30),
						new GunSmithProfession.BuyForItemsFactory(Items.IRON_INGOT, 3, HWGItems.SMG, 1, 16, 30),
						new GunSmithProfession.BuyForItemsFactory(Items.IRON_INGOT, 3, HWGItems.TOMMYGUN, 1, 16, 30) },
				4,
				new TradeOffers.Factory[] {
						new GunSmithProfession.BuyForItemsFactory(HWGItems.FUEL_TANK, 1, 4, HWGItems.FLAMETHROWER, 1,
								16, 40),
						new GunSmithProfession.BuyForItemsFactory(Items.IRON_INGOT, 6, 4, HWGItems.SHOTGUN, 1, 16, 40),
						new GunSmithProfession.BuyForItemsFactory(Items.GUNPOWDER, 8, 4, HWGItems.BULLETS, 48, 16,
								50) },
				5,
				new TradeOffers.Factory[] {
						new GunSmithProfession.BuyForItemsFactory(Items.IRON_INGOT, 18, 8, HWGItems.ROCKETLAUNCHER, 1,
								16, 60),
						new GunSmithProfession.BuyForItemsFactory(Items.IRON_INGOT, 18, 8, HWGItems.G_LAUNCHER, 1, 16,
								60),
						new GunSmithProfession.BuyForItemsFactory(Items.IRON_INGOT, 18, 8, HWGItems.SNIPER, 1, 16,
								60) })));
	}

	public static Int2ObjectMap<TradeOffers.Factory[]> copyToFastUtilMap(
			ImmutableMap<Integer, TradeOffers.Factory[]> immutableMap) {
		return new Int2ObjectOpenHashMap<Factory[]>(immutableMap);
	}

	public static class BuyForItemsFactory implements TradeOffers.Factory {
		private final ItemStack secondBuy;
		private final int secondCount;
		private final int price;
		private final ItemStack sell;
		private final int sellCount;
		private final int maxUses;
		private final int experience;
		private final float multiplier;

		public BuyForItemsFactory(ItemConvertible item, int secondCount, Item sellItem, int sellCount, int maxUses,
				int experience) {
			this(item, secondCount, 1, sellItem, sellCount, maxUses, experience);
		}

		public BuyForItemsFactory(ItemConvertible item, int secondCount, int price, Item sellItem, int sellCount,
				int maxUses, int experience) {
			this.secondBuy = new ItemStack(item);
			this.secondCount = secondCount;
			this.price = price;
			this.sell = new ItemStack(sellItem);
			this.sellCount = sellCount;
			this.maxUses = maxUses;
			this.experience = experience;
			this.multiplier = 0.05F;
		}

		@Nullable
		public TradeOffer create(Entity entity, RandomGenerator random) {
			return new TradeOffer(new ItemStack(Items.EMERALD, this.price),
					new ItemStack(this.secondBuy.getItem(), this.secondCount),
					new ItemStack(this.sell.getItem(), this.sellCount), this.maxUses, this.experience, this.multiplier);
		}
	}

	static class BuyForOneEmeraldFactory implements TradeOffers.Factory {
		private final Item buy;
		private final int price;
		private final int maxUses;
		private final int experience;
		private final float multiplier;

		public BuyForOneEmeraldFactory(ItemConvertible item, int price, int maxUses, int experience) {
			this.buy = item.asItem();
			this.price = price;
			this.maxUses = maxUses;
			this.experience = experience;
			this.multiplier = 0.05F;
		}

		public TradeOffer create(Entity entity, RandomGenerator random) {
			ItemStack itemStack = new ItemStack(this.buy, this.price);
			return new TradeOffer(itemStack, new ItemStack(Items.EMERALD), this.maxUses, this.experience,
					this.multiplier);
		}
	}

	static class BuyForItemFactory implements TradeOffers.Factory {
		private final Item buy;
		private final Item sell;
		private final int price;
		private final int maxUses;
		private final int experience;
		private final float multiplier;

		public BuyForItemFactory(ItemConvertible item, ItemConvertible sell, int price, int maxUses, int experience) {
			this.buy = item.asItem();
			this.sell = sell.asItem();
			this.price = price;
			this.maxUses = maxUses;
			this.experience = experience;
			this.multiplier = 0.05F;
		}

		public TradeOffer create(Entity entity, RandomGenerator random) {
			ItemStack itemStack = new ItemStack(this.buy, this.price);
			return new TradeOffer(itemStack, new ItemStack(sell.asItem()), this.maxUses, this.experience,
					this.multiplier);
		}
	}

	static class SellItemFactory implements TradeOffers.Factory {
		private final ItemStack sell;
		private final int price;
		private final int count;
		private final int maxUses;
		private final int experience;
		private final float multiplier;

		public SellItemFactory(Block block, int price, int count, int maxUses, int experience) {
			this(new ItemStack(block), price, count, maxUses, experience);
		}

		public SellItemFactory(Item item, int price, int count, int experience) {
			this((ItemStack) (new ItemStack(item)), price, count, 12, experience);
		}

		public SellItemFactory(Item item, int price, int count, int maxUses, int experience) {
			this(new ItemStack(item), price, count, maxUses, experience);
		}

		public SellItemFactory(ItemStack itemStack, int price, int count, int maxUses, int experience) {
			this(itemStack, price, count, maxUses, experience, 0.05F);
		}

		public SellItemFactory(ItemStack itemStack, int price, int count, int maxUses, int experience,
				float multiplier) {
			this.sell = itemStack;
			this.price = price;
			this.count = count;
			this.maxUses = maxUses;
			this.experience = experience;
			this.multiplier = multiplier;
		}

		public TradeOffer create(Entity entity, RandomGenerator random) {
			return new TradeOffer(new ItemStack(Items.EMERALD, this.price),
					new ItemStack(this.sell.getItem(), this.count), this.maxUses, this.experience, this.multiplier);
		}
	}

}
