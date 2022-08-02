package io.github.zemelua.umu_little_maid.sound;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ModSounds {
	public static final Marker MARKER = MarkerManager.getMarker("SOUND").addParents(UMULittleMaid.MARKER);

	public static final SoundEvent ENTITY_MAID_GENERAL_AMBIENT;
	public static final SoundEvent ENTITY_MAID_GENERAL_FENCER_ATTACK;
	public static final SoundEvent ENTITY_MAID_GENERAL_CRACKER_ATTACK;
	public static final SoundEvent ENTITY_MAID_GENERAL_ARCHER_ATTACK;
	public static final SoundEvent ENTITY_MAID_GENERAL_KILLED;
	public static final SoundEvent ENTITY_MAID_GENERAL_KILLED_BARELY;
	public static final SoundEvent ENTITY_MAID_GENERAL_HURT;
	public static final SoundEvent ENTITY_MAID_GENERAL_DEATH;
	public static final SoundEvent ENTITY_MAID_GENERAL_EAT;
	public static final SoundEvent ENTITY_MAID_GENERAL_CONTRACT;
	public static final SoundEvent ENTITY_MAID_BRAVERY_FENCER_ATTACK;
	public static final SoundEvent ENTITY_MAID_BRAVERY_HURT;
	public static final SoundEvent ENTITY_MAID_BRAVERY_CONTRACT;
	public static final SoundEvent ENTITY_MAID_DILIGENT_CONTRACT;
	public static final SoundEvent ENTITY_MAID_AUDACIOUS_DEATH;
	public static final SoundEvent ENTITY_MAID_AUDACIOUS_CONTRACT;
	public static final SoundEvent ENTITY_MAID_GENTLE_CONTRACT;
	public static final SoundEvent ENTITY_MAID_SHY_CONTRACT;
	public static final SoundEvent ENTITY_MAID_LAZY_AMBIENT;
	public static final SoundEvent ENTITY_MAID_LAZY_DEATH;
	public static final SoundEvent ENTITY_MAID_LAZY_CONTRACT;
	public static final SoundEvent ENTITY_MAID_TSUNDERE_CONTRACT;

	private static boolean initialized = false;
	public static void initialize() {
		if (ModSounds.initialized) throw new IllegalStateException("Sounds are already initialized!");

		Registry.register(Registry.SOUND_EVENT, "entity.maid.general.ambient", ModSounds.ENTITY_MAID_GENERAL_AMBIENT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.general.fencer_attack", ModSounds.ENTITY_MAID_GENERAL_FENCER_ATTACK);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.general.cracker_attack", ModSounds.ENTITY_MAID_GENERAL_CRACKER_ATTACK);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.general.archer_attack", ModSounds.ENTITY_MAID_GENERAL_ARCHER_ATTACK);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.general.killed", ModSounds.ENTITY_MAID_GENERAL_KILLED);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.general.killed_barely", ModSounds.ENTITY_MAID_GENERAL_KILLED_BARELY);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.general.hurt", ModSounds.ENTITY_MAID_GENERAL_HURT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.general.death", ModSounds.ENTITY_MAID_GENERAL_DEATH);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.general.eat", ModSounds.ENTITY_MAID_GENERAL_EAT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.general.contract", ModSounds.ENTITY_MAID_GENERAL_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.bravery.fencer_attack", ModSounds.ENTITY_MAID_BRAVERY_FENCER_ATTACK);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.bravery.hurt", ModSounds.ENTITY_MAID_BRAVERY_HURT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.bravery.contract", ModSounds.ENTITY_MAID_BRAVERY_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.diligent.contract", ModSounds.ENTITY_MAID_DILIGENT_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.audacious.death", ModSounds.ENTITY_MAID_AUDACIOUS_DEATH);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.audacious.contract", ModSounds.ENTITY_MAID_AUDACIOUS_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.gentle.contract", ModSounds.ENTITY_MAID_GENTLE_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.shy.contract", ModSounds.ENTITY_MAID_SHY_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.lazy.ambient", ModSounds.ENTITY_MAID_LAZY_AMBIENT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.lazy.death", ModSounds.ENTITY_MAID_LAZY_DEATH);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.lazy.contract", ModSounds.ENTITY_MAID_LAZY_CONTRACT);
		Registry.register(Registry.SOUND_EVENT, "entity.maid.tsundere.contract", ModSounds.ENTITY_MAID_TSUNDERE_CONTRACT);

		ModSounds.initialized = true;
		UMULittleMaid.LOGGER.info(ModSounds.MARKER, "Sounds are initialized!");
	}

	private ModSounds() throws IllegalAccessException {throw new IllegalAccessException();}

	static {
		ENTITY_MAID_GENERAL_AMBIENT = new SoundEvent(UMULittleMaid.identifier("entity.maid.general.ambient"));
		ENTITY_MAID_GENERAL_FENCER_ATTACK = new SoundEvent(UMULittleMaid.identifier("entity.maid.general.fencer_attack"));
		ENTITY_MAID_GENERAL_CRACKER_ATTACK = new SoundEvent(UMULittleMaid.identifier("entity.maid.general.cracker_attack"));
		ENTITY_MAID_GENERAL_ARCHER_ATTACK = new SoundEvent(UMULittleMaid.identifier("entity.maid.general.archer_attack"));
		ENTITY_MAID_GENERAL_KILLED = new SoundEvent(UMULittleMaid.identifier("entity.maid.general.killed"));
		ENTITY_MAID_GENERAL_KILLED_BARELY = new SoundEvent(UMULittleMaid.identifier("entity.maid.general.killed_barely"));
		ENTITY_MAID_GENERAL_HURT = new SoundEvent(UMULittleMaid.identifier("entity.maid.general.hurt"));
		ENTITY_MAID_GENERAL_DEATH = new SoundEvent(UMULittleMaid.identifier("entity.maid.general.death"));
		ENTITY_MAID_GENERAL_EAT = new SoundEvent(UMULittleMaid.identifier("entity.maid.general.eat"));
		ENTITY_MAID_GENERAL_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.general.contract"));
		ENTITY_MAID_BRAVERY_FENCER_ATTACK = new SoundEvent(UMULittleMaid.identifier("entity.maid.bravery.fencer_attack"));
		ENTITY_MAID_BRAVERY_HURT = new SoundEvent(UMULittleMaid.identifier("entity.maid.bravery.hurt"));
		ENTITY_MAID_BRAVERY_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.bravery.contract"));
		ENTITY_MAID_DILIGENT_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.diligent.contract"));
		ENTITY_MAID_AUDACIOUS_DEATH = new SoundEvent(UMULittleMaid.identifier("entity.maid.audacious.death"));
		ENTITY_MAID_AUDACIOUS_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.audacious.contract"));
		ENTITY_MAID_GENTLE_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.gentle.contract"));
		ENTITY_MAID_SHY_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.shy.contract"));
		ENTITY_MAID_LAZY_AMBIENT = new SoundEvent(UMULittleMaid.identifier("entity.maid.lazy.ambient"));
		ENTITY_MAID_LAZY_DEATH = new SoundEvent(UMULittleMaid.identifier("entity.maid.lazy.death"));
		ENTITY_MAID_LAZY_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.lazy.contract"));
		ENTITY_MAID_TSUNDERE_CONTRACT = new SoundEvent(UMULittleMaid.identifier("entity.maid.tsundere.contract"));
	}
}
