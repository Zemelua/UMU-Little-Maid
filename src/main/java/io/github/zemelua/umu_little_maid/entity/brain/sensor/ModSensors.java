package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModSensors {
	public static final SensorType<AvailableBedSensor> AVAILABLE_BED = new SensorType<>(AvailableBedSensor::new);
	public static final SensorType<MaidAttackableSensor> SENSOR_MAID_ATTACKABLE = new SensorType<>(MaidAttackableSensor::new);
	public static final SensorType<MaidAttractableLivingsSensor> SENSOR_MAID_ATTRACTABLE_LIVINGS = new SensorType<>(MaidAttractableLivingsSensor::new);
	public static final SensorType<MaidGuardableLivingSensor> SENSOR_MAID_GUARDABLE_LIVING = new SensorType<>(MaidGuardableLivingSensor::new);
	public static final SensorType<MaidFarmablePosesSensor> SENSOR_MAID_FARMABLE_POSES = new SensorType<>(MaidFarmablePosesSensor::new);
	public static final SensorType<FishableWatersSensor> SENSOR_FISHABLE_WATERS = new SensorType<>(FishableWatersSensor::new);
	public static final SensorType<HomeCandidateSensor> SENSOR_HOME_CANDIDATE = new SensorType<>(HomeCandidateSensor::new);

	public static void init() {
		Registry.register(Registries.SENSOR_TYPE, UMULittleMaid.identifier("available_bed"), AVAILABLE_BED);
		Registry.register(Registries.SENSOR_TYPE, UMULittleMaid.identifier("maid_attackable"), ModSensors.SENSOR_MAID_ATTACKABLE);
		Registry.register(Registries.SENSOR_TYPE, UMULittleMaid.identifier("maid_attractable_livings"), ModSensors.SENSOR_MAID_ATTRACTABLE_LIVINGS);
		Registry.register(Registries.SENSOR_TYPE, UMULittleMaid.identifier("maid_guardable_living"), ModSensors.SENSOR_MAID_GUARDABLE_LIVING);
		Registry.register(Registries.SENSOR_TYPE, UMULittleMaid.identifier("maid_farmable_poses"), ModSensors.SENSOR_MAID_FARMABLE_POSES);
		Registry.register(Registries.SENSOR_TYPE, UMULittleMaid.identifier("maid_fishable_waters"), SENSOR_FISHABLE_WATERS);
		Registry.register(Registries.SENSOR_TYPE, UMULittleMaid.identifier("home"), ModSensors.SENSOR_HOME_CANDIDATE);
	}

	private ModSensors() {}
}
