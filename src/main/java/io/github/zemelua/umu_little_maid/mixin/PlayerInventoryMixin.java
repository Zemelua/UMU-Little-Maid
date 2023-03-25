package io.github.zemelua.umu_little_maid.mixin;

import io.github.zemelua.umu_little_maid.c_component.Components;
import io.github.zemelua.umu_little_maid.c_component.IInstructionComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Nameable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Environment(value= EnvType.CLIENT)
@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory, Nameable {
	@Inject(method = "scrollInHotbar", at = @At("HEAD"))
	private void onMouseScroll(double scrollAmount, CallbackInfo callback) {
		ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
		IInstructionComponent instructionComponent = player.getComponent(Components.INSTRUCTION);

		if (instructionComponent.isInstructing()) {
			// ClientPlayNetworking.send(NetworkHandler.CHANNEL_CLIENT_INSTRUCTION_CANCEL, PacketByteBufs.create());
		}
	}
}
