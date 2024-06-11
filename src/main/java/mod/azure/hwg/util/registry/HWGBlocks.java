package mod.azure.hwg.util.registry;

import mod.azure.hwg.HWGMod;
import mod.azure.hwg.blocks.FuelTankBlock;
import mod.azure.hwg.blocks.GunTableBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public record HWGBlocks() {

    public static final Block FUEL_TANK = block(new FuelTankBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).noOcclusion()), "fuel_tank");
    public static final GunTableBlock GUN_TABLE = block(new GunTableBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(4.0f).noOcclusion()), "gun_table");

    static <T extends Block> T block(T c, String id) {
        Registry.register(BuiltInRegistries.BLOCK, HWGMod.modResource(id), c);
        return c;
    }

    public static void initialize() {
    }
}
