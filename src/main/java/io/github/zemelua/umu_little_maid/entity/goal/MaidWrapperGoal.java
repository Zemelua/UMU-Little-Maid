package io.github.zemelua.umu_little_maid.entity.goal;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.job.MaidJob;
import io.github.zemelua.umu_little_maid.entity.maid.personality.MaidPersonality;
import net.minecraft.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class MaidWrapperGoal extends Goal {
	private final LittleMaidEntity maid;
	private final Predicate<LittleMaidEntity>[] predicates;
	private final Goal goal;

	private MaidWrapperGoal(LittleMaidEntity maid, Goal goal, Predicate<LittleMaidEntity>[] predicates) {
		this.maid = maid;
		this.predicates = predicates;
		this.goal = goal;

		this.setControls(this.goal.getControls());
	}

	@Override
	public boolean canStart() {
		return Arrays.stream(this.predicates).anyMatch(jobPredicate -> jobPredicate.test(this.maid))
				&& this.goal.canStart();
	}

	@Override
	public boolean shouldContinue() {
		return Arrays.stream(this.predicates).anyMatch(jobPredicate -> jobPredicate.test(this.maid))
				&& this.goal.shouldContinue();
	}

	@Override
	public boolean canStop() {
		return Arrays.stream(this.predicates).anyMatch(jobPredicate -> jobPredicate.test(this.maid))
				&& this.goal.canStop();
	}

	@Override
	public void start() {
		this.goal.start();
	}

	@Override
	public void stop() {
		this.goal.stop();
	}

	@Override
	public boolean shouldRunEveryTick() {
		return this.goal.shouldRunEveryTick();
	}

	@Override
	public void tick() {
		this.goal.tick();
	}

	@Override
	public void setControls(EnumSet<Control> controls) {
		this.goal.setControls(controls);
	}

	@Override
	public String toString() {
		return this.goal.toString();
	}

	@Override
	public EnumSet<Control> getControls() {
		return this.goal.getControls();
	}

	@Override
	protected int getTickCount(int ticks) {
		return this.shouldRunEveryTick() ? ticks : Goal.toGoalTicks(ticks);
	}

	public static class Builder {
		private final LittleMaidEntity maid;
		private final Goal goal;
		private final List<Predicate<LittleMaidEntity>> predicates = new ArrayList<>();

		public Builder(LittleMaidEntity maid, Goal goal) {
			this.maid = maid;
			this.goal = goal;
		}

		public Builder addPredicate(Predicate<LittleMaidEntity> predicate) {
			this.predicates.add(predicate);

			return this;
		}

		public Builder addJob(MaidJob... jobs) {
			for (MaidJob job : jobs) {
				this.predicates.add((maid -> job == maid.getJob()));
			}

			return this;
		}

		public Builder addPersonality(MaidPersonality... personalities) {
			for (MaidPersonality personality : personalities) {
				this.predicates.add((maid -> personality == maid.getPersonality()));
			}

			return this;
		}

		@SuppressWarnings("unchecked")
		public MaidWrapperGoal build() {
			return new MaidWrapperGoal(this.maid, this.goal, this.predicates.toArray(new Predicate[0]));
		}
	}
}
