package io.github.zemelua.umu_little_maid.entity.brain;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModActivities {
	public static final Activity SIT = new Activity("sit");
	public static final Activity GUARD = new Activity("guard");
	public static final Activity EAT = new Activity("eat");
	public static final Activity FARM = new Activity("farm");
	public static final Activity HEAL = new Activity("heal");
	public static final Activity GO_GET_TRIDENT = new Activity("go_get_trident");
	public static final Activity BREATH = new Activity("breath");
	public static final Activity SHEAR_SHEEP = new Activity("shear_sheep");  // 未使用

	public static void init() {
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("sit"), ModActivities.SIT);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("guard"), ModActivities.GUARD);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("eat"), ModActivities.EAT);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("farm"), ModActivities.FARM);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("heal"), ModActivities.HEAL);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("go_get_trident"), ModActivities.GO_GET_TRIDENT);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("breath"), ModActivities.BREATH);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("shear_sheep"), ModActivities.SHEAR_SHEEP);
	}

	private ModActivities() {}
}
