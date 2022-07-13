package io.github.zemelua.umu_little_maid.inventory;

import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.client.screen.LittleMaidScreen;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class LittleMaidScreenHandler extends ScreenHandler {
	public static final Text TITLE = Text.translatable("container.umu_little_maid.little_maid");

	private final LittleMaidEntity maid;

	protected LittleMaidScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf packet) {
		this(syncId, inventory, (LittleMaidEntity) inventory.player.getWorld().getEntityById(packet.readInt()));
	}

	public LittleMaidScreenHandler(int syncId, PlayerInventory inventory, LittleMaidEntity maid) {
		super(ModInventories.LITTLE_MAID, syncId);

		this.maid = maid;

		// index : 0
		this.addSlot(this.new MaidHeldItemSlot(8, 18));

		// index : 1-2
		for (int i = 0; i < LittleMaidEntity.ARMORS.length; i++) {
			this.addSlot(new MaidArmorSlot(LittleMaidEntity.ARMORS[i], 8, 36 + i * 18));
		}

		// index : 3-17
		Inventory maidInventory = this.maid.getInventory();
		int size = maidInventory.size(); // 15
		for (int i = 0; i < 3 && (i + 1) * 5 <= size; i++) {
			for (int j = 0; j < 5 && i * 5 + j + 1 <= size; j++) {
				this.addSlot(new Slot(maidInventory, i * 5 + j, 80 + j * 18, 18 + i * 18));
			}
		}

		// index : 18-44
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(inventory, 9 + i * 9 + j, 8 + j * 18, 84 + i * 18));
			}
		}

		// index : 45-53
		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
		}

		this.addListener(new ScreenHandlerListener() {
			@Override
			public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack itemStack) {
				if (slotId == 0) maid.setStackInHand(Hand.MAIN_HAND, itemStack);
				else if (slotId == 1) maid.equipStack(EquipmentSlot.HEAD, itemStack);
				else if (slotId == 2) maid.equipStack(EquipmentSlot.FEET, itemStack);
			}

			@Override
			public void onPropertyUpdate(ScreenHandler handler, int property, int value) {

			}
		});
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;

		Slot slot = this.slots.get(index);
		if (slot.hasStack()) {
			ItemStack clickedStack = slot.getStack();
			itemStack = clickedStack.copy();

			if (index >= 0 && index < 18) {
				if (!this.insertItem(clickedStack, 18, 54, false)) return ItemStack.EMPTY;
			} else if (index >= 18 && index < 54) {
				if (!this.insertItem(clickedStack, 3, 18, false)) return ItemStack.EMPTY;
			}

			if (clickedStack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}

		return itemStack;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.maid.getInventory().canPlayerUse(player) && this.maid.isAlive() && this.maid.distanceTo(player) < 8.0f;
	}

	public LittleMaidEntity getMaid() {
		return this.maid;
	}

	private class MaidHeldItemSlot extends Slot {
		public MaidHeldItemSlot(int x, int y) {
			super(new SimpleInventory(LittleMaidScreenHandler.this.maid.getMainHandStack()), 0, x, y);
		}

		@Environment(EnvType.CLIENT)
		@Nullable
		@Override
		public Pair<Identifier, Identifier> getBackgroundSprite() {
			return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, LittleMaidScreen.EMPTY_HELD_SLOT_TEXTURE);
		}
	}

	private class MaidArmorSlot extends Slot {
		private final EquipmentSlot slot;

		public MaidArmorSlot(EquipmentSlot slot, int x, int y) {
			super(new SimpleInventory(LittleMaidScreenHandler.this.maid.getEquippedStack(slot)), 0, x, y);
			this.slot = slot;
		}

		@Override
		public boolean canInsert(ItemStack itemStack) {
			return super.canInsert(itemStack) && this.slot == MobEntity.getPreferredEquipmentSlot(itemStack);
		}

		@Override
		public boolean canTakeItems(PlayerEntity player) {
			return !(player.isCreative() && EnchantmentHelper.hasBindingCurse(this.getStack())) && super.canTakeItems(player);
		}

		@Nullable
		@Override
		public Pair<Identifier, Identifier> getBackgroundSprite() {
			return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
					LittleMaidScreen.EMPTY_ARMOR_SLOT_TEXTURES[this.slot.getEntitySlotId()]);
		}
	}
}
