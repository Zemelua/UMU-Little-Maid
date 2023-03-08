package io.github.zemelua.umu_little_maid;

import io.github.zemelua.umu_little_maid.c_component.Components;
import io.github.zemelua.umu_little_maid.c_component.IInstructionComponent;
import io.github.zemelua.umu_little_maid.util.InstructionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public final class Callbacks {
	public static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		IInstructionComponent instructionComponent = player.getComponent(Components.INSTRUCTION);
		if (instructionComponent.isInstructing()) {
			return instructionComponent.tryInstruction(world, hitResult);
		}

		return ActionResult.PASS;
	}

	public static ActionResult onUseEntity(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
		IInstructionComponent instructionComponent = InstructionUtils.getComponent(player);
		if (hitResult == null) return ActionResult.PASS;

		if (instructionComponent.isInstructing()) {
			return instructionComponent.tryInstruction(world, hitResult);
		}

		return ActionResult.PASS;
	}
}
