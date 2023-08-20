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
	public static final Activity SHEAR = new Activity("shear");
	public static final Activity DELIVER = new Activity("deliver");

	public static void init() {
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("sit"), SIT);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("guard"), GUARD);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("eat"), EAT);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("farm"), FARM);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("heal"), HEAL);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("go_get_trident"), GO_GET_TRIDENT);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("breath"), BREATH);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("shear"), SHEAR);
		Registry.register(Registries.ACTIVITY, UMULittleMaid.identifier("deliver"), DELIVER);
	}

	private ModActivities() {}
}
