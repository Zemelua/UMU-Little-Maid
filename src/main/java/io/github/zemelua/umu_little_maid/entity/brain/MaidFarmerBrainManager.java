package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.KeepAroundHomeOrAnchorTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.ShelterFromRainTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.MaidEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.UpdateShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.MaidHarvestTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.MaidPlantTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.UpdateFarmPosTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.WalkToFarmPosTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.deliver.MaidDeliverTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.deliver.MaidWalkToBoxTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.deliver.UpdateDeliveryBoxTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.look.LookAtEntityTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.MaidSleepTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.UpdateShouldSleepTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.UpdateSleepPosTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.WalkToSleepPosTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.tameable.FollowMasterTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.tameable.SitTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.tameable.TeleportToMasterTask;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public final class MaidFarmerBrainManager {
	public static void initBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid) {
		addCoreTasks(brain);
		addIdleTasks(brain);
		addSitTasks(brain);
		addEatTasks(brain);
		addFarmTasks(brain);
		addDeliverTasks(brain);
		addSleepTasks(brain);

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
	}

	public static void tickBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid) {
		ImmutableList.Builder<Activity> activities = ImmutableList.builder();
		activities.add(ModActivities.SIT);
		activities.add(ModActivities.EAT);
		activities.add(Activity.REST);
		activities.add(ModActivities.FARM);
		if (maid.hasHarvests() && (!brain.hasMemoryModule(ModMemories.FARM_POS) || !maid.hasEmptySlot())) {
			activities.add(ModActivities.DELIVER);
		}
		activities.add(Activity.IDLE);

		brain.resetPossibleActivities(activities.build());
	}

	private static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				Pair.of(0, new StayAboveWaterTask(0.8F)),
				Pair.of(0, OpenDoorsTask.create()),
				Pair.of(0, WakeUpTask.create()),
				Pair.of(0, new KeepAroundHomeOrAnchorTask()),
				Pair.of(1, new LookAroundTask(45, 90)),
				Pair.of(2, new WanderAroundTask()),
				Pair.of(99, new UpdateShouldEatTask<>()),
				Pair.of(99, new UpdateShouldSleepTask<>()),
				Pair.of(99, new UpdateSleepPosTask()),
				Pair.of(99, new UpdateFarmPosTask<>()),
				Pair.of(99, new UpdateDeliveryBoxTask<>())
		));
	}

	@SuppressWarnings("deprecation")
	private static void addIdleTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.IDLE, ImmutableList.of(
				Pair.of(0, new FollowMasterTask<>(10.0F)),
				Pair.of(0, new TeleportToMasterTask<>(15.0F)),
				Pair.of(1, new ShelterFromRainTask<>()),
				Pair.of(2, LookAtMobWithIntervalTask.follow(EntityType.PLAYER, 6.0f, UniformIntProvider.create(30, 60))),
				Pair.of(3, new RandomTask<>(ImmutableList.of(
						Pair.of(StrollTask.create(0.8F), 2),
						Pair.of(GoTowardsLookTargetTask.create(0.8F, 3), 2),
						Pair.of(new WaitTask(30, 60), 1)
				))),
				Pair.of(3, new RandomTask<>(ImmutableList.of(
						Pair.of(new LookAtEntityTask<>((self, target) -> target.equals(self.getMaster().orElse(null))), 1),
						Pair.of(new WaitTask(30, 60), 4)
				)))
		));
	}

	private static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.SIT, ImmutableList.of(
				Pair.of(0, new SitTask()),
				Pair.of(5, new RandomTask<>(ImmutableList.of(
						Pair.of(new LookAtEntityTask<>((self, target) -> target.equals(self.getMaster().orElse(null))), 8),
						Pair.of(LookAtMobTask.create(EntityType.VILLAGER, 8.0f), 1),
						Pair.of(LookAtMobTask.create(EntityType.PLAYER, 8.0f), 1),
						Pair.of(LookAtMobTask.create(SpawnGroup.CREATURE, 8.0f), 3),
						Pair.of(LookAtMobTask.create(SpawnGroup.WATER_CREATURE, 8.0f), 1),
						Pair.of(LookAtMobTask.create(SpawnGroup.AXOLOTLS, 8.0f), 2),
						Pair.of(LookAtMobTask.create(SpawnGroup.UNDERGROUND_WATER_CREATURE, 8.0f), 1),
						Pair.of(LookAtMobTask.create(SpawnGroup.WATER_AMBIENT, 8.0f), 1),
						Pair.of(new WaitTask(30, 60), 5)
				)))
		), ImmutableSet.of(
				Pair.of(ModMemories.IS_SITTING, MemoryModuleState.VALUE_PRESENT)
		));
	}

	private static void addEatTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.EAT, ImmutableList.of(
				Pair.of(0, new MaidEatTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_EAT, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
		), ImmutableSet.of(ModMemories.SHOULD_EAT));
	}

	private static void addFarmTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.FARM, ImmutableList.of(
				Pair.of(0, new MaidHarvestTask()),
				Pair.of(0, new MaidPlantTask()),
				Pair.of(1, new WalkToFarmPosTask<>(1.0F))
		), ImmutableSet.of(
				Pair.of(ModMemories.FARM_POS, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
		));
	}

	private static void addDeliverTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.DELIVER, ImmutableList.of(
				Pair.of(0, new MaidDeliverTask<>(ModTags.ITEM_MAID_HARVESTS)),
				Pair.of(1, new MaidWalkToBoxTask<>())
		), ImmutableSet.of(
				Pair.of(ModMemories.DELIVERY_BOX, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
		));
	}

	public static void addSleepTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.REST, ImmutableList.of(
				Pair.of(0, new MaidSleepTask()),
				Pair.of(1, new WalkToSleepPosTask<>())
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_SLEEP, MemoryModuleState.VALUE_PRESENT),
				Pair.of(ModMemories.SLEEP_POS, MemoryModuleState.VALUE_PRESENT)
		));
	}

	private MaidFarmerBrainManager() {}

	public static boolean isPlantable(BlockPos pos, ServerWorld world) {
		BlockState blockState = world.getBlockState(pos);
		BlockState downState = world.getBlockState(pos.down());

		return downState.getBlock() instanceof FarmlandBlock && blockState.isAir();
	}

	public static boolean isHarvestable(BlockPos pos, ServerWorld world) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();

		if (blockState.isIn(ModTags.BLOCK_MAID_HARVESTS)) {
			if (block instanceof CropBlock crop) {
				return crop.isMature(blockState);
			}

			return true;
		}

		return false;
	}

	public static boolean isGourd(BlockPos pos, ServerWorld world) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();

		if (blockState.isIn(ModTags.BLOCK_MAID_GOURDS)) {
			if (block instanceof GourdBlock gourd) {
				for (int i = 0; i < 4; i++) {
					Direction direction = Direction.fromHorizontal(i);
					BlockState sideState = world.getBlockState(pos.offset(direction));
					if (sideState.isOf(gourd.getAttachedStem())) {
						try {
							if (sideState.get(AttachedStemBlock.FACING).equals(direction.getOpposite())) {
								return true;
							}
						} catch (IllegalArgumentException exception) {
							UMULittleMaid.LOGGER.info(ModEntities.MARKER, "不明な原因でBlockからFACINGを得られなかったよ！");
						}
					}
				}

				return false;
			}

			return true;
		}

		return false;
	}
}
