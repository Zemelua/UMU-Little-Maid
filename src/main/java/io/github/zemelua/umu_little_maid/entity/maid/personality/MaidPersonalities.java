package io.github.zemelua.umu_little_maid.entity.maid.personality;

import java.util.ArrayList;
import java.util.List;

public class MaidPersonalities {
	public static final MaidPersonality BRAVERY = new MaidPersonality(new MaidPersonality.Builder()
			.setLeap()
	);
	public static final MaidPersonality TSUNDERE = new MaidPersonality(new MaidPersonality.Builder()
			.setCurt().setFollowStartDistance(13.8D).setFollowStopDistance(5.0D)
	);
	public static final MaidPersonality SHY = new ShyPersonality(new MaidPersonality.Builder()
			.setFollowStartDistance(12.0D).setFollowStopDistance(5.17D)
	);

	private static final List<MaidPersonality> REGISTRY = new ArrayList<>() {
		{
			add(BRAVERY);
			add(TSUNDERE);
			add(SHY);
		}
	};

	public static MaidPersonality getByID(int id) {
		MaidPersonality personality = REGISTRY.get(id);
		if (personality == null) {
			personality = BRAVERY;
		}

		return personality;
	}

	public static int getID(MaidPersonality personality) {
		for (int i = 0; i < REGISTRY.size(); i++) {
			if (REGISTRY.get(i) == personality) return i;
		}

		return 0;
	}
}
