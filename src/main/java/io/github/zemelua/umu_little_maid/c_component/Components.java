package io.github.zemelua.umu_little_maid.c_component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import org.jetbrains.annotations.NotNull;

public final class Components implements EntityComponentInitializer {
	public static final ComponentKey<IInstructionComponent> INSTRUCTION;

	@Override
	public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(INSTRUCTION, InstructionComponent::new, RespawnCopyStrategy.NEVER_COPY);
	}

	static {
		INSTRUCTION = ComponentRegistryV3.INSTANCE.getOrCreate(UMULittleMaid.identifier("instruction"), IInstructionComponent.class);
	}
}
