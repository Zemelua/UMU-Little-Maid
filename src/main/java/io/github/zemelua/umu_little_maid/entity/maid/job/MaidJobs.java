package io.github.zemelua.umu_little_maid.entity.maid.job;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.brain.*;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import net.minecraft.registry.Registry;

import java.util.stream.Stream;

public final class MaidJobs {
	public static final IMaidJob NONE;
	public static final IMaidJob FENCER;
	public static final IMaidJob CRACKER;
	public static final IMaidJob ARCHER;
	public static final IMaidJob HUNTER;
	public static final IMaidJob POSEIDON;
	public static final IMaidJob GUARD;
	public static final IMaidJob HEALER;
	public static final IMaidJob FARMER;
	public static final IMaidJob FISHER;
	public static final IMaidJob SHEPHERD;

	public static void init() {
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("none"), NONE);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("fencer"), FENCER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("cracker"), CRACKER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("archer"), ARCHER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("hunter"), HUNTER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("poseidon"), POSEIDON);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("guard"), GUARD);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("healer"), HEALER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("farmer"), FARMER);
		// Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("fisher"), FISHER);
		// Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("shepherd"), SHEPHERD);
	}

	public static Stream<IMaidJob> getAllJobs() {
		return ModRegistries.MAID_JOB.stream();
	}

	static {
		NONE = new NoneJob(MaidNoneBrainManager::initBrain, MaidNoneBrainManager::tickBrain);
		FENCER = new BasicMaidJob(ModTags.ITEM_MAID_FENCER_TOOLS, MaidFencerBrainManager::initBrain, MaidFencerBrainManager::tickBrain);
		CRACKER = new BasicMaidJob(ModTags.ITEM_MAID_CRACKER_TOOLS, MaidCrackerBrainManager::initBrain, MaidCrackerBrainManager::tickBrain);
		ARCHER = new BasicMaidJob(ModTags.ITEM_MAID_ARCHER_TOOLS, MaidArcherBrainManager::initBrain, MaidArcherBrainManager::tickBrain);
		HUNTER = new BasicMaidJob(ModTags.ITEM_MAID_HUNTER_TOOLS, MaidHunterBrainManager::initBrain, MaidHunterBrainManager::tickBrain);
		POSEIDON = new PoseidonJob(ModTags.ITEM_MAID_POSEIDON_TOOLS, MaidPoseidonBrainManager::initBrain, MaidPoseidonBrainManager::tickBrain);
		GUARD = new BasicMaidJob(ModTags.ITEM_MAID_GUARD_TOOLS, MaidGuardBrainManager::initBrain, MaidGuardBrainManager::tickBrain);
		HEALER = new BasicMaidJob(ModTags.ITEM_MAID_HEALER_TOOLS, MaidHealerBrainManager::initBrain, MaidHealerBrainManager::tickBrain);
		FARMER = new BasicMaidJob(ModTags.ITEM_MAID_FARMER_TOOLS, MaidFarmerBrainManager::initBrain, MaidFarmerBrainManager::tickBrain);
		FISHER = new BasicMaidJob(ModTags.ITEM_MAID_FISHER_TOOLS, MaidFisherBrainManager::initBrain, MaidFisherBrainManager::tickBrain);
		SHEPHERD = new BasicMaidJob(ModTags.ITEM_MAID_SHEPHERD_TOOLS, MaidShepherdBrainManager::initBrain, MaidShepherdBrainManager::tickBrain);
	}

	private MaidJobs() {}
}
