package io.github.zemelua.umu_little_maid.entity.maid;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.Map;

public final class MaidSpawnHandler {
	private static final Map<MaidPersonality, Integer> PLAINS_ODDS;
	private static final Map<MaidPersonality, Integer> DESERT_ODDS;
	private static final Map<MaidPersonality, Integer> SWAMP_ODDS;
	private static final Map<MaidPersonality, Integer> FOREST_ODDS;
	private static final Map<MaidPersonality, Integer> TAIGA_ODDS;
	private static final Map<MaidPersonality, Integer> SAVANNA_ODDS;
	private static final Map<MaidPersonality, Integer> GROVE_ODDS;
	private static final Map<MaidPersonality, Integer> CAVES_ODDS;

	public static MaidPersonality randomPersonality(Random random, RegistryEntry<Biome> biome) {
		if (biome.matchesKey(BiomeKeys.PLAINS) || biome.matchesKey(BiomeKeys.SUNFLOWER_PLAINS) || biome.matchesKey(BiomeKeys.SNOWY_PLAINS)
				|| biome.matchesKey(BiomeKeys.ICE_SPIKES) || biome.matchesKey(BiomeKeys.MEADOW)) {
			return MaidSpawnHandler.randomPersonality(random, MaidSpawnHandler.PLAINS_ODDS);
		} else if (biome.matchesKey(BiomeKeys.DESERT)) {
			return MaidSpawnHandler.randomPersonality(random, MaidSpawnHandler.DESERT_ODDS);
		} else if (biome.matchesKey(BiomeKeys.SWAMP)) {
			return MaidSpawnHandler.randomPersonality(random, MaidSpawnHandler.SWAMP_ODDS);
		} else if (biome.matchesKey(BiomeKeys.FOREST) || biome.matchesKey(BiomeKeys.FLOWER_FOREST) || biome.matchesKey(BiomeKeys.BIRCH_FOREST)
				|| biome.matchesKey(BiomeKeys.DARK_FOREST) || biome.matchesKey(BiomeKeys.OLD_GROWTH_BIRCH_FOREST)) {
			return MaidSpawnHandler.randomPersonality(random, MaidSpawnHandler.FOREST_ODDS);
		} else if (biome.matchesKey(BiomeKeys.OLD_GROWTH_PINE_TAIGA) || biome.matchesKey(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA)
				|| biome.matchesKey(BiomeKeys.TAIGA) || biome.matchesKey(BiomeKeys.SNOWY_TAIGA)) {
			return MaidSpawnHandler.randomPersonality(random, MaidSpawnHandler.TAIGA_ODDS);
		} else if (biome.matchesKey(BiomeKeys.SAVANNA) || biome.matchesKey(BiomeKeys.SAVANNA_PLATEAU) || biome.matchesKey(BiomeKeys.JUNGLE)
				|| biome.matchesKey(BiomeKeys.SPARSE_JUNGLE) || biome.matchesKey(BiomeKeys.BAMBOO_JUNGLE)) {
			return MaidSpawnHandler.randomPersonality(random, MaidSpawnHandler.SAVANNA_ODDS);
		} else if (biome.matchesKey(BiomeKeys.GROVE) || biome.matchesKey(BiomeKeys.BADLANDS) || biome.matchesKey(BiomeKeys.ERODED_BADLANDS)
				|| biome.matchesKey(BiomeKeys.WOODED_BADLANDS)) {
			return MaidSpawnHandler.randomPersonality(random, MaidSpawnHandler.GROVE_ODDS);
		} else if (biome.matchesKey(BiomeKeys.LUSH_CAVES)) {
			return MaidSpawnHandler.randomPersonality(random, MaidSpawnHandler.CAVES_ODDS);
		}

		return MaidSpawnHandler.randomPersonality(random, MaidSpawnHandler.PLAINS_ODDS);
	}

	public static MaidPersonality randomPersonality(Random random, Map<MaidPersonality, Integer> odds) {
		int entirety = odds.values().stream().mapToInt(i -> i).sum();
		int odd = random.nextInt(entirety);

		int currentOdds = 0;
		for (Map.Entry<MaidPersonality, Integer> entry : odds.entrySet()) {
			currentOdds += entry.getValue();
			if (odd < currentOdds) return entry.getKey();
		}

		return ModEntities.PERSONALITY_BRAVERY;
	}

	private MaidSpawnHandler() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	static {
		PLAINS_ODDS = ImmutableMap.of(
				ModEntities.PERSONALITY_BRAVERY, 100,
				ModEntities.PERSONALITY_DILIGENT, 100,
				ModEntities.PERSONALITY_AUDACIOUS, 100,
				ModEntities.PERSONALITY_GENTLE, 90,
				ModEntities.PERSONALITY_SHY, 30,
				ModEntities.PERSONALITY_LAZY, 5,
				ModEntities.PERSONALITY_TSUNDERE, 6
		);
		DESERT_ODDS = ImmutableMap.of(
				ModEntities.PERSONALITY_BRAVERY, 70,
				ModEntities.PERSONALITY_DILIGENT, 70,
				ModEntities.PERSONALITY_AUDACIOUS, 100,
				ModEntities.PERSONALITY_GENTLE, 40,
				ModEntities.PERSONALITY_SHY, 5,
				ModEntities.PERSONALITY_LAZY, 5,
				ModEntities.PERSONALITY_TSUNDERE, 6
		);
		SWAMP_ODDS = ImmutableMap.of(
				ModEntities.PERSONALITY_BRAVERY, 30,
				ModEntities.PERSONALITY_DILIGENT, 40,
				ModEntities.PERSONALITY_AUDACIOUS, 30,
				ModEntities.PERSONALITY_GENTLE, 40,
				ModEntities.PERSONALITY_SHY, 50,
				ModEntities.PERSONALITY_LAZY, 100,
				ModEntities.PERSONALITY_TSUNDERE, 30
		);
		FOREST_ODDS = ImmutableMap.of(
				ModEntities.PERSONALITY_BRAVERY, 30,
				ModEntities.PERSONALITY_DILIGENT, 40,
				ModEntities.PERSONALITY_AUDACIOUS, 40,
				ModEntities.PERSONALITY_GENTLE, 80,
				ModEntities.PERSONALITY_SHY, 100,
				ModEntities.PERSONALITY_LAZY, 60,
				ModEntities.PERSONALITY_TSUNDERE, 10
		);
		TAIGA_ODDS = ImmutableMap.of(
				ModEntities.PERSONALITY_BRAVERY, 20,
				ModEntities.PERSONALITY_DILIGENT, 90,
				ModEntities.PERSONALITY_AUDACIOUS, 60,
				ModEntities.PERSONALITY_GENTLE, 70,
				ModEntities.PERSONALITY_SHY, 20,
				ModEntities.PERSONALITY_LAZY, 10,
				ModEntities.PERSONALITY_TSUNDERE, 100
		);
		SAVANNA_ODDS = ImmutableMap.of(
				ModEntities.PERSONALITY_BRAVERY, 100,
				ModEntities.PERSONALITY_DILIGENT, 20,
				ModEntities.PERSONALITY_AUDACIOUS, 80,
				ModEntities.PERSONALITY_GENTLE, 20,
				ModEntities.PERSONALITY_SHY, 5,
				ModEntities.PERSONALITY_LAZY, 3,
				ModEntities.PERSONALITY_TSUNDERE, 5
		);
		GROVE_ODDS = ImmutableMap.of(
				ModEntities.PERSONALITY_BRAVERY, 50,
				ModEntities.PERSONALITY_DILIGENT, 20,
				ModEntities.PERSONALITY_AUDACIOUS, 50,
				ModEntities.PERSONALITY_GENTLE, 40,
				ModEntities.PERSONALITY_SHY, 20,
				ModEntities.PERSONALITY_LAZY, 10,
				ModEntities.PERSONALITY_TSUNDERE, 10
		);
		CAVES_ODDS = ImmutableMap.of(
				ModEntities.PERSONALITY_BRAVERY, 20,
				ModEntities.PERSONALITY_DILIGENT, 30,
				ModEntities.PERSONALITY_AUDACIOUS, 10,
				ModEntities.PERSONALITY_GENTLE, 80,
				ModEntities.PERSONALITY_SHY, 100,
				ModEntities.PERSONALITY_LAZY, 90,
				ModEntities.PERSONALITY_TSUNDERE, 90
		);
	}
}
