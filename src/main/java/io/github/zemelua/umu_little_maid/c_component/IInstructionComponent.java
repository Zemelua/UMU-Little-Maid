package io.github.zemelua.umu_little_maid.c_component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.entity.Entity;

import java.util.Optional;

public interface IInstructionComponent extends ComponentV3 {
	void startInstruction(Entity target);

	void finishInstruction();

	boolean isInstructing();

	Optional<Entity> getTarget();
}
