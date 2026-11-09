package dev.anton.aeroindustrial.registry;

import dev.anton.aeroindustrial.AeroIndustrial;
import dev.anton.aeroindustrial.recipe.AeroMachineRecipe;
import dev.anton.aeroindustrial.recipe.AeroMachineRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AeroRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, AeroIndustrial.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AeroMachineRecipe>> AERO_MACHINE_TYPE =
            RECIPE_TYPES.register("aero_machine", RecipeType::simple);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, AeroIndustrial.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, AeroMachineRecipeSerializer> AERO_MACHINE_SERIALIZER =
            RECIPE_SERIALIZERS.register("aero_machine", AeroMachineRecipeSerializer::new);

    private AeroRecipes() {}
}
