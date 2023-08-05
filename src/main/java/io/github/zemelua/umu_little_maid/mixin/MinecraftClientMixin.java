package io.github.zemelua.umu_little_maid.mixin;

import io.github.zemelua.umu_little_maid.util.InstructionUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> implements WindowEventHandler {
	@Shadow @Nullable public ClientPlayerEntity player;
	@Shadow public int attackCooldown;

	@Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
	private void doAttack(CallbackInfoReturnable<Boolean> callback) {
		if (InstructionUtils.getComponent(Objects.requireNonNull(this.player)).isInstructing()) {
			InstructionUtils.finishOnClient();
			this.attackCooldown = 10;
			this.player.swingHand(Hand.MAIN_HAND);
			callback.setReturnValue(false);
		}
	}

	private MinecraftClientMixin(String string) {
		super(string);
	}
}
