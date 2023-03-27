package io.github.zemelua.umu_little_maid.tinker;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class EarlyRiser implements Runnable {
	private static final String NAMESPACE_INTERMEDIARY = "intermediary";

	private static final String ENTITY_POSE_SRG_NAME = "net.minecraft.class_4050";
	public static final String ENTITY_POSE_EATING = "EATING";
	public static final String ENTITY_POSE_USING_DRIPLEAF = "USING_DRIPLEAF";
	public static final String ENTITY_POSE_CHANGING_COSTUME = "CHANGING_COSTUME";
	public static final String ENTITY_POSE_HEALING = "HEALING";

	private static final String ARM_POSE_SRG_NAME = "net.minecraft.class_572$class_573";
	static final String ARM_POSE_HEADPATTING = "HEADPATTING";

	@Override
	public void run() {
		MappingResolver reMapper = FabricLoader.getInstance().getMappingResolver();

		String entityPoseName = reMapper.mapClassName(NAMESPACE_INTERMEDIARY, ENTITY_POSE_SRG_NAME);
		ClassTinkerers.enumBuilder(entityPoseName)
				.addEnum(ENTITY_POSE_EATING)
				.addEnum(ENTITY_POSE_USING_DRIPLEAF)
				.addEnum(ENTITY_POSE_CHANGING_COSTUME)
				.addEnum(ENTITY_POSE_HEALING).build();

		String armPoseName = reMapper.mapClassName(NAMESPACE_INTERMEDIARY, ARM_POSE_SRG_NAME);
		ClassTinkerers.enumBuilder(armPoseName, boolean.class)
				.addEnum(ARM_POSE_HEADPATTING, false).build();
	}
}
