package io.github.zemelua.umu_little_maid.entity.maid;

import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.ModPOIs;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestStorage.OccupationStatus;
import net.minecraft.world.spawner.Spawner;

import java.util.Optional;
import java.util.stream.Stream;

public class MaidSpawner implements Spawner {
	private static final int INTERVAL = 600;

	private int cooldown;

	@Override
	public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
		if (!spawnAnimals) return 0;

		this.cooldown--;
		if (this.cooldown > 0) return 0;
		this.cooldown = INTERVAL;

		Optional<ServerPlayerEntity> player = Optional.ofNullable(world.getRandomAlivePlayer());
		if (player.isEmpty()) return 0;

		Random random = world.getRandom();
		PointOfInterestStorage poiStorage = world.getPointOfInterestStorage();
		Stream<PointOfInterest> pois = poiStorage.getInCircle(poi -> poi.matchesKey(ModPOIs.POI_DESSERT), player.get().getBlockPos(), 48, OccupationStatus.ANY);

		long spawnCount = pois.filter(poi -> {
			int x = (8 + random.nextInt(24)) * (random.nextBoolean() ? 1 : -1);
			int z = (8 + random.nextInt(24)) * (random.nextBoolean() ? 1 : -1);
			BlockPos pos = poi.getPos().add(x, 0, z);

			if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, world, pos, ModEntities.LITTLE_MAID)
					&& world.getNonSpectatingEntities(LittleMaidEntity.class, new Box(pos).expand(48.0, 8.0, 48.0)).size() < 5) {
				return this.spawn(world, pos);
			}

			return false;
		}).count();

		return spawnCount <= Integer.MAX_VALUE
				? (int) spawnCount
				: Integer.MAX_VALUE;
	}

	private boolean spawn(ServerWorld world, BlockPos pos) {
		return ModEntities.LITTLE_MAID.spawn(world, null, null, null, pos, SpawnReason.EVENT, false, false) != null;
	}
}
