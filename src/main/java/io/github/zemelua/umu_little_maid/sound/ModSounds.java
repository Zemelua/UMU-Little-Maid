package io.github.zemelua.umu_little_maid.sound;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ModSounds {
	public static final Marker MARKER = MarkerManager.getMarker("SOUND").addParents(UMULittleMaid.MARKER);

	public static final SoundEvent ENTITY_MAID_GENERAL_CONTRACT;
	public static final SoundEvent ENTITY_MAID_BRAVERY_CONTRACT;
	public static final SoundEvent ENTITY_MAID_DILIGENT_CONTRACT;
	public static final SoundEvent ENTITY_MAID_AUDACIOUS_CONTRACT;
	public static final SoundEvent ENTITY_MAID_GENTLE_CONTRACT;
	public static final SoundEvent ENTITY_MAID_SHY_CONTRACT;
	public static final SoundEvent ENTITY_MAID_LAZY_CONTRACT;
	public static final SoundEvent ENTITY_MAID_TSUNDERE_CONTRACT;

	private ModSounds() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	private static boolean initialized = false;

	public static void initialize() {
		if (ModSounds.initialized) throw new IllegalStateException("Sounds are already initialized!");

		Registry.register(Registry.SOUND_EVENT, "entity.maid.general.contract", ModSounds.ENTITY_MAID_GENERAL_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.bravery.contract", ModSounds.ENTITY_MAID_BRAVERY_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.diligent.contract", ModSounds.ENTITY_MAID_DILIGENT_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.audacious.contract", ModSounds.ENTITY_MAID_AUDACIOUS_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.gentle.contract", ModSounds.ENTITY_MAID_GENTLE_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.shy.contract", ModSounds.ENTITY_MAID_SHY_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.lazy.contract", ModSounds.ENTITY_MAID_LAZY_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.tsundere.contract", ModSounds.ENTITY_MAID_TSUNDERE_CONTRACT);

		ModSounds.initialized = true;
		UMULittleMaid.LOGGER.info(ModSounds.MARKER, "Items are initialized!");
	}

	static {
		ENTITY_MAID_GENERAL_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.general.contract"));
		ENTITY_MAID_BRAVERY_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.bravery.contract"));
		ENTITY_MAID_DILIGENT_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.diligent.contract"));
		ENTITY_MAID_AUDACIOUS_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.audacious.contract"));
		ENTITY_MAID_GENTLE_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.gentle.contract"));
		ENTITY_MAID_SHY_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.shy.contract"));
		ENTITY_MAID_LAZY_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.lazy.contract"));
		ENTITY_MAID_TSUNDERE_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.tsundere.contract"));
	}
}
