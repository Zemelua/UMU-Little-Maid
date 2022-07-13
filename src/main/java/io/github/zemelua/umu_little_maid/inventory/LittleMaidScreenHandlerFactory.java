package io.github.zemelua.umu_little_maid.inventory;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("ClassCanBeRecord")
public class LittleMaidScreenHandlerFactory implements ExtendedScreenHandlerFactory {
	private final LittleMaidEntity maid;

	public LittleMaidScreenHandlerFactory(LittleMaidEntity maid) {
		this.maid = maid;
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf packet) {
		int id = this.maid.getId();

		packet.writeInt(id);
	}

	@Override
	public Text getDisplayName() {
		return LittleMaidScreenHandler.TITLE;
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
		return new LittleMaidScreenHandler(syncId, inventory, this.maid);
	}
}
