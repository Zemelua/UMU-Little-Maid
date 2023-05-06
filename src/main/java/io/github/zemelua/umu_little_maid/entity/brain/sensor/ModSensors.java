package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.util.registry.Registry;

public final class ModSensors {
	public static final SensorType<AvailableBedSensor> AVAILABLE_BED = new SensorType<>(AvailableBedSensor::new);

	public static void init() {
		Registry.register(Registry.SENSOR_TYPE, UMULittleMaid.identifier("available_bed"), AVAILABLE_BED);
	}

	private ModSensors() {}
}
