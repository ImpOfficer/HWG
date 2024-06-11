package mod.azure.hwg.util.recipes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.azure.hwg.HWGMod;
import mod.azure.hwg.client.gui.GunTableInventory;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public record GunTableRecipe(List<Pair<Ingredient, Integer>> ingredients, ItemStack output) implements Recipe<GunTableInventory>, Comparable<GunTableRecipe> {

    @Override
    public boolean matches(GunTableInventory inv, Level world) {
        for (int i = 0; i < this.ingredients.size(); i++) {
            var slotStack = inv.getItem(i);
            var pair = ingredients.get(i);
            var ingredient = pair.getFirst();
            var count = pair.getSecond();
            if (slotStack.getCount() < count || !(ingredient.test(slotStack)))
                return false;
        }
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(GunTableInventory craftingContainer, HolderLookup.Provider registries) {
        return this.getResultItem(registries).copy();
    }

    public Ingredient getIngredientForSlot(int index) {
        return ingredients.get(index).getFirst();
    }

    public int countRequired(int index) {
        return ingredients.get(index).getSecond();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return HWGMod.GUN_TABLE_RECIPE_SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public int compareTo(@NotNull GunTableRecipe o) {
        var outputThis = output.getItem();
        var outputOther = o.output.getItem();
        return BuiltInRegistries.ITEM.getKey(outputThis).compareTo(BuiltInRegistries.ITEM.getKey(outputOther));
    }

    public static class Type implements RecipeType<GunTableRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "gun_table";

        private Type() {
        }
    }

    public static class Serializer implements RecipeSerializer<GunTableRecipe> {
        public static Serializer INSTANCE = new Serializer();

        private Serializer() {
        }

        public static final MapCodec<GunTableRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.list(
                        Codec.mapPair(Ingredient.CODEC_NONEMPTY.fieldOf("ingredient"),
                        Codec.INT.fieldOf("count")).codec()).fieldOf("ingredients").forGetter(i -> i.ingredients),
                ItemStack.CODEC.fieldOf("result").forGetter(i -> i.output)
        ).apply(inst, GunTableRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, GunTableRecipe> STREAM_CODEC = StreamCodec.of(
                GunTableRecipe.Serializer::toNetwork, GunTableRecipe.Serializer::fromNetwork);

        @Override
        public @NotNull MapCodec<GunTableRecipe> codec() {
            return MAP_CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, GunTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static GunTableRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            List<Pair<Ingredient, Integer>> list = buffer.readList(buf -> Pair.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), buffer.readInt()));
            ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
            return new GunTableRecipe(list, output);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, GunTableRecipe recipe) {
            buffer.writeCollection(recipe.ingredients, (buf, pair) -> {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, pair.getFirst());
                buf.writeInt(pair.getSecond());
            });
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
        }
    }
}
