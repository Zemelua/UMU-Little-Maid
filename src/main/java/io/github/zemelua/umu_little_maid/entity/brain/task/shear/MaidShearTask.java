package io.github.zemelua.umu_little_maid.entity.brain.task.shear;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.api.BetterMultiTickTask;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.ILittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.action.MaidAction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.world.event.GameEvent;

import java.util.Optional;

public class MaidShearTask<M extends LivingEntity & ILittleMaidEntity> extends BetterMultiTickTask<M> {
	public MaidShearTask() {
		super(ImmutableMap.of(ModMemories.SHEAR_TARGET, MemoryModuleState.VALUE_PRESENT), 30);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, M maid) {
		if (!maid.canAction()) return false;

		Brain<?> brain = maid.getBrain();
		ShearableMobWrapper<?> shearable = brain.getOptionalRegisteredMemory(ModMemories.SHEAR_TARGET).orElseThrow();

		return shearable.get().distanceTo(maid) < 2.0F;
	}

	@Override
	protected void run(ServerWorld world, M maid, long time) {
		maid.setAction(MaidAction.SHEARING);

		Brain<?> brain = maid.getBrain();
		ShearableMobWrapper<?> shearable = brain.getOptionalRegisteredMemory(ModMemories.SHEAR_TARGET).orElseThrow();
		brain.remember(ModMemories.SHEAR_TARGET, shearable);
		LookTargetUtil.lookAt(maid, shearable.get());
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, M maid, long time) {
		Brain<?> brain = maid.getBrain();
		Optional<ShearableMobWrapper<?>> shearable = brain.getOptionalRegisteredMemory(ModMemories.SHEAR_TARGET);

		return shearable.filter(IShearableWrapper::isShearable).isPresent();
	}

	@Override
	protected void keepRunning(ServerWorld world, M maid, long time) {
		Brain<?> brain = maid.getBrain();
		ShearableMobWrapper<?> shearable = brain.getOptionalRegisteredMemory(ModMemories.SHEAR_TARGET).orElseThrow();

		if (this.getPassedTicks(time) == 20L) {

		} else if (this.getPassedTicks(time) == 30L) {
			shearable.sheared(SoundCategory.NEUTRAL);
			shearable.get().emitGameEvent(GameEvent.SHEAR, maid);
			ItemStack shears = maid.getMainHandStack();
			if (shears.isOf(Items.SHEARS)) {
				shears.damage(1, maid, m -> m.sendToolBreakStatus(Hand.MAIN_HAND));
			}
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, M maid, long time) {
		maid.removeAction();

		Brain<?> brain = maid.getBrain();
		brain.forget(ModMemories.SHEAR_TARGET);
		brain.forget(MemoryModuleType.LOOK_TARGET);
		brain.forget(MemoryModuleType.WALK_TARGET);
	}
}
