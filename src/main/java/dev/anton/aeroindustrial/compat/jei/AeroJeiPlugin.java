package dev.anton.aeroindustrial.compat.jei;

import dev.anton.aeroindustrial.AeroIndustrial;
import dev.anton.aeroindustrial.registry.AeroItems;
import dev.anton.aeroindustrial.registry.AeroRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

@JeiPlugin
public final class AeroJeiPlugin implements IModPlugin {
    private static final java.util.function.Supplier<RecipeType<RecipeHolder<dev.anton.aeroindustrial.recipe.AeroMachineRecipe>>> JEI_TYPE =
            RecipeType.createFromDeferredVanilla(AeroRecipes.AERO_MACHINE_TYPE);

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(AeroIndustrial.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AeroMachineCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level != null) {
            registration.addRecipes(
                    JEI_TYPE.get(),
                    level.getRecipeManager().getAllRecipesFor(AeroRecipes.AERO_MACHINE_TYPE.get())
            );
        }
    }

    private static final class AeroMachineCategory implements IRecipeCategory<RecipeHolder<dev.anton.aeroindustrial.recipe.AeroMachineRecipe>> {
        private final IDrawable icon;

        private AeroMachineCategory(IGuiHelper guiHelper) {
            this.icon = guiHelper.createDrawableIngredient(
                    VanillaTypes.ITEM_STACK,
                    AeroItems.AEROFLUX_CRYSTAL.get().getDefaultInstance()
            );
        }

        @Override
        public RecipeType<RecipeHolder<dev.anton.aeroindustrial.recipe.AeroMachineRecipe>> getRecipeType() {
            return JEI_TYPE.get();
        }

        @Override
        public Component getTitle() {
            return Component.translatable("jei.aeroindustrial.machine");
        }

        @Override
        public int getWidth() {
            return 150;
        }

        @Override
        public int getHeight() {
            return 70;
        }

        @Override
        public IDrawable getIcon() {
            return icon;
        }

        @Override
        public void setRecipe(
                IRecipeLayoutBuilder builder,
                RecipeHolder<dev.anton.aeroindustrial.recipe.AeroMachineRecipe> holder,
                IFocusGroup focuses
        ) {
            var recipe = holder.value();
            builder.addInputSlot(18, 22).addIngredients(recipe.input());
            builder.addOutputSlot(104, 22).addItemStack(recipe.result());
        }

        @Override
        public void draw(
                RecipeHolder<dev.anton.aeroindustrial.recipe.AeroMachineRecipe> holder,
                IRecipeSlotsView recipeSlotsView,
                GuiGraphics graphics,
                double mouseX,
                double mouseY
        ) {
            var recipe = holder.value();
            graphics.fill(0, 0, 150, 70, 0xFF171B20);
            graphics.fill(4, 4, 146, 66, 0xFF242A31);
            graphics.drawString(
                    Minecraft.getInstance().font,
                    Component.translatable("jei.aeroindustrial.machine.tier", recipe.machineTier()),
                    8, 8, 0xFFE7ECF0, false
            );
            graphics.drawString(
                    Minecraft.getInstance().font,
                    Component.translatable("jei.aeroindustrial.time", recipe.processingTime()),
                    8, 48, 0xFF9FCBFF, false
            );
            graphics.drawString(
                    Minecraft.getInstance().font,
                    Component.translatable("jei.aeroindustrial.energy", recipe.energyCost()),
                    8, 58, 0xFF71E0FF, false
            );
        }
    }
}
