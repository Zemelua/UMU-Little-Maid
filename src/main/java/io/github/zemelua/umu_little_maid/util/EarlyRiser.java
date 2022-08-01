package io.github.zemelua.umu_little_maid.util;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class EarlyRiser implements Runnable {
	public static final String ENTITY_POSE_EATING = "EATING";
	public static final String ENTITY_POSE_USING_DRIPLEAF = "USING_DRIPLEAF";
	public static final String ENTITY_POSE_CHANGING_COSTUME = "CHANGING_COSTUME";
	public static final String ENTITY_POSE_HEALING = "HEALING";

	@Override
	public void run() {
		MappingResolver reMapper = FabricLoader.getInstance().getMappingResolver();
		String entityPoseName = reMapper.mapClassName("intermediary", "net.minecraft.class_4050");
		ClassTinkerers.enumBuilder(entityPoseName)
				.addEnum(EarlyRiser.ENTITY_POSE_EATING)
				.addEnum(EarlyRiser.ENTITY_POSE_USING_DRIPLEAF)
				.addEnum(EarlyRiser.ENTITY_POSE_CHANGING_COSTUME)
				.addEnum(EarlyRiser.ENTITY_POSE_HEALING).build();
	}
}
