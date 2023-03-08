package io.github.zemelua.umu_little_maid.network;

import io.github.zemelua.umu_little_maid.c_component.Components;
import io.github.zemelua.umu_little_maid.c_component.IInstructionComponent;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PacketHandlers {
	static void handleInstructionCancel(ServerPlayerEntity player) {
		IInstructionComponent instructionComponent = player.getComponent(Components.INSTRUCTION);
		instructionComponent.cancelInstruction();
	}

	private PacketHandlers() {}
}
