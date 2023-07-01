package io.github.zemelua.umu_little_maid.entity.brain;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModActivities {
	public static final Activity ACTIVITY_SIT = new Activity("sit");
	public static final Activity ACTIVITY_GUARD = new Activity("guard");
	public static final Activity ACTIVITY_EAT = new Activity("eat");
	public static final Activity ACTIVITY_FARM = new Activity("farm");
	public static final Activity ACTIVITY_HEAL = new Activity("heal");
	public static final Activity ACTIVITY_GO_GET_TRIDENT = new Activity("go_get_trident");
	public static final Activity ACTIVITY_BREATH = new Activity("breath");
	public static final Activity ACTIVITY_SHEAR_SHEEP = new Activity("shear_sheep");  // 未使用

	public static void init() {
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("sit"), ModActivities.ACTIVITY_SIT);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("guard"), ModActivities.ACTIVITY_GUARD);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("eat"), ModActivities.ACTIVITY_EAT);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("farm"), ModActivities.ACTIVITY_FARM);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("heal"), ModActivities.ACTIVITY_HEAL);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("go_get_trident"), ModActivities.ACTIVITY_GO_GET_TRIDENT);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("breath"), ModActivities.ACTIVITY_BREATH);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("shear_sheep"), ModActivities.ACTIVITY_SHEAR_SHEEP);
	}

	private ModActivities() {}
}
