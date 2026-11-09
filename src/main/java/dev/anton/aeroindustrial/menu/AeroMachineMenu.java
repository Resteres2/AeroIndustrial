package dev.anton.aeroindustrial.menu;

import dev.anton.aeroindustrial.block.AeroMachineBlockEntity;
import dev.anton.aeroindustrial.registry.AeroBlocks;
import dev.anton.aeroindustrial.registry.AeroMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class AeroMachineMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerLevelAccess access;
    private final BlockPos blockPos;

    public AeroMachineMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, new SimpleContainer(2), ContainerLevelAccess.NULL, data.readBlockPos());
    }

    public AeroMachineMenu(int containerId, Inventory playerInventory, AeroMachineBlockEntity machine) {
        this(containerId, playerInventory, machine,
                machine.getLevel() == null
                        ? ContainerLevelAccess.NULL
                        : ContainerLevelAccess.create(machine.getLevel(), machine.getBlockPos()),
                machine.getBlockPos());
    }

    private AeroMachineMenu(int containerId, Inventory playerInventory, Container container,
                            ContainerLevelAccess access, BlockPos blockPos) {
        super(AeroMenus.AERO_MACHINE.get(), containerId);
        this.container = container;
        this.access = access;
        this.blockPos = blockPos;

        checkContainerSize(container, 2);

        this.addSlot(new Slot(container, 0, 44, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !(container instanceof AeroMachineBlockEntity machine) || machine.canAcceptInput(stack);
            }
        });

        this.addSlot(new Slot(container, 1, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        this.addStandardInventorySlots(playerInventory, 8, 84);
    }

    public Container getMachineContainer() {
        return container;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate((level, pos) ->
                level.getBlockEntity(pos) instanceof AeroMachineBlockEntity
                        && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0,
                true);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) {
            return result;
        }

        ItemStack stack = slot.getItem();
        result = stack.copy();

        if (index == 0 || index == 1) {
            if (!this.moveItemStackTo(stack, 2, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        slot.onTake(player, stack);
        return result;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
