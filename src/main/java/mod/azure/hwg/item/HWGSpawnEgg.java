package mod.azure.hwg.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public class HWGSpawnEgg extends SpawnEggItem {

	public HWGSpawnEgg(EntityType<? extends Mob> type) {
		super(type, 11022961, 11035249, new Item.Properties().stacksTo(1));
	}

}