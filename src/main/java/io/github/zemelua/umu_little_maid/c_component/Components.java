package io.github.zemelua.umu_little_maid.c_component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.c_component.headpatted.DependentHeadpattedComponent;
import io.github.zemelua.umu_little_maid.c_component.headpatted.IHeadpattedComponent;
import io.github.zemelua.umu_little_maid.c_component.headpatting.HeadpattingComponent;
import io.github.zemelua.umu_little_maid.c_component.headpatting.IHeadpattingComponent;
import io.github.zemelua.umu_little_maid.c_component.instruction.IInstructionComponent;
import io.github.zemelua.umu_little_maid.c_component.instruction.InstructionComponent;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import org.jetbrains.annotations.NotNull;

public final class Components implements EntityComponentInitializer {
	public static final ComponentKey<IInstructionComponent> INSTRUCTION;
	public static final ComponentKey<IHeadpattingComponent> HEADPATTING;
	public static final ComponentKey<IHeadpattedComponent> HEADPATTED;

	@Override
	public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(INSTRUCTION, InstructionComponent::new, RespawnCopyStrategy.NEVER_COPY);
		registry.registerForPlayers(HEADPATTING, HeadpattingComponent::new, RespawnCopyStrategy.NEVER_COPY);

		registry.registerFor(LittleMaidEntity.class, HEADPATTED, DependentHeadpattedComponent::new);
	}

	static {
		INSTRUCTION = ComponentRegistryV3.INSTANCE.getOrCreate(UMULittleMaid.identifier("instruction"), IInstructionComponent.class);
		HEADPATTING = ComponentRegistryV3.INSTANCE.getOrCreate(UMULittleMaid.identifier("headpatting"), IHeadpattingComponent.class);
		HEADPATTED = ComponentRegistryV3.INSTANCE.getOrCreate(UMULittleMaid.identifier("headpatted"), IHeadpattedComponent.class);
	}
}
