package io.github.zemelua.umu_little_maid.network;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.c_component.headpatting.HeadpattingManager;
import io.github.zemelua.umu_little_maid.c_component.headpatting.IHeadpattingComponent;
import io.github.zemelua.umu_little_maid.c_component.instruction.IInstructionComponent;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.InstructionUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.Optional;

public final class PacketHandlers {
	static void handleInstructionCancel(ServerPlayerEntity player) {
		IInstructionComponent component = InstructionUtils.getComponent(player);
		component.cancelInstruction();
	}

	static void handleStartHeadpatting(ServerPlayerEntity player, World world, int maidID) {
		IHeadpattingComponent component = HeadpattingManager.getComponent(player);
		Optional<LittleMaidEntity> maid = Optional.ofNullable(world.getEntityById(maidID))
				.filter(e -> e instanceof LittleMaidEntity)
				.map(e -> (LittleMaidEntity) e);

		maid.ifPresentOrElse(component::startHeadpatting, () -> UMULittleMaid.LOGGER.error("パケットからメイドさんが見つかんない！"));
	}

	static void handleFinishHeadpatting(ServerPlayerEntity player) {
		IHeadpattingComponent component = HeadpattingManager.getComponent(player);
		component.finishHeadpatting();
	}

	private PacketHandlers() {}
}
