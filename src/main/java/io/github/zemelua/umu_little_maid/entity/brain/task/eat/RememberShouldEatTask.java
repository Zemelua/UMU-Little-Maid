package io.github.zemelua.umu_little_maid.entity.brain.task.eat;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

import java.util.Map;
import java.util.function.Predicate;

public class RememberShouldEatTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_SHOULD_EAT, MemoryModuleState.VALUE_ABSENT
	);

	private final Predicate<LittleMaidEntity> postpone;

	public RememberShouldEatTask() {
		this(living -> false);
	}

	public RememberShouldEatTask(Predicate<LittleMaidEntity> postpone) {
		super(RememberShouldEatTask.REQUIRED_MEMORIES, 0);

		this.postpone = postpone;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		return RememberShouldEatTask.shouldEat(maid, this.postpone);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		brain.remember(ModEntities.MEMORY_SHOULD_EAT, Unit.INSTANCE);
	}

	public static boolean shouldEat(LittleMaidEntity maid, Predicate<LittleMaidEntity> postpone) {
		if (MaidEatTask.searchHealItems(maid).isEmpty()) return false;

		if (postpone.test(maid)) {
			return maid.getHealth() < maid.getMaxHealth() * 0.4F;
		} else {
			return maid.getHealth() < maid.getMaxHealth();
		}
	}
}