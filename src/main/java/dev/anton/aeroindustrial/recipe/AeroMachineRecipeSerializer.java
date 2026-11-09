package dev.anton.aeroindustrial.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class AeroMachineRecipeSerializer implements RecipeSerializer<AeroMachineRecipe> {
    public static final MapCodec<AeroMachineRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.INT.fieldOf("machine_tier").forGetter(AeroMachineRecipe::machineTier),
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(AeroMachineRecipe::input),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(AeroMachineRecipe::result),
            com.mojang.serialization.Codec.INT.fieldOf("processing_time").forGetter(AeroMachineRecipe::processingTime),
            com.mojang.serialization.Codec.INT.fieldOf("energy_cost").forGetter(AeroMachineRecipe::energyCost)
    ).apply(instance, AeroMachineRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AeroMachineRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    AeroMachineRecipe::machineTier,
                    Ingredient.CONTENTS_STREAM_CODEC,
                    AeroMachineRecipe::input,
                    ItemStack.STREAM_CODEC,
                    AeroMachineRecipe::result,
                    ByteBufCodecs.VAR_INT,
                    AeroMachineRecipe::processingTime,
                    ByteBufCodecs.VAR_INT,
                    AeroMachineRecipe::energyCost,
                    AeroMachineRecipe::new
            );

    @Override
    public MapCodec<AeroMachineRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AeroMachineRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
