package io.github.zemelua.umu_little_maid.c_component.instruction;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.Optional;

public interface IInstructionComponent extends ComponentV3, ServerTickingComponent {
	void startInstruction(LittleMaidEntity target);
	ActionResult tryInstruction(World world, BlockHitResult target);
	ActionResult tryInstruction(World world, EntityHitResult target);
	void finishInstruction();
	Optional<LittleMaidEntity> getTarget();

	default boolean isInstructing() {
		return this.getTarget().isPresent();
	}
}
