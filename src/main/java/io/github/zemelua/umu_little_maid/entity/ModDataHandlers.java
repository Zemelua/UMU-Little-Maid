package io.github.zemelua.umu_little_maid.entity;

import io.github.zemelua.umu_little_maid.entity.maid.action.MaidAction;
import io.github.zemelua.umu_little_maid.entity.maid.attack.MaidAttackType;
import io.github.zemelua.umu_little_maid.entity.maid.feeling.IMaidFeeling;
import io.github.zemelua.umu_little_maid.entity.maid.job.IMaidJob;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.GlobalPos;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class ModDataHandlers {
	public static final TrackedDataHandler<Optional<Integer>> OPTIONAL_INT;
	public static final TrackedDataHandler<Collection<GlobalPos>> COLLECTION_GLOBAL_POS;
	public static final TrackedDataHandler<IMaidJob> MAID_JOB;
	public static final TrackedDataHandler<Optional<MaidAction>> OPTIONAL_MAID_ACTION;
	public static final TrackedDataHandler<IMaidFeeling> MAID_FEELING;
	public static final TrackedDataHandler<MaidAttackType> MAID_ATTACK_TYPE;

	public static void init() {
		TrackedDataHandlerRegistry.register(OPTIONAL_INT);
		TrackedDataHandlerRegistry.register(COLLECTION_GLOBAL_POS);
		TrackedDataHandlerRegistry.register(MAID_JOB);
		TrackedDataHandlerRegistry.register(MAID_FEELING);
		TrackedDataHandlerRegistry.register(MAID_ATTACK_TYPE);
	}

	static {
		OPTIONAL_INT = TrackedDataHandler.ofOptional(PacketByteBuf::writeInt, PacketByteBuf::readInt);
		COLLECTION_GLOBAL_POS = TrackedDataHandler.of((packet, collection) -> {
			packet.writeInt(collection.size());
			for (GlobalPos pos : collection) {
				packet.writeGlobalPos(pos);
			}
		}, packet -> {
			Set<GlobalPos> set = new HashSet<>();
			int size = packet.readInt();
			for (int i = 0; i < size; i++) {
				set.add(packet.readGlobalPos());
			}

			return set;
		});
		MAID_JOB = TrackedDataHandler.of(ModRegistries.MAID_JOB);
		OPTIONAL_MAID_ACTION = TrackedDataHandler.ofOptional(PacketByteBuf::writeEnumConstant, packet -> packet.readEnumConstant(MaidAction.class));
		MAID_FEELING = TrackedDataHandler.of(ModRegistries.MAID_FEELING);
		MAID_ATTACK_TYPE = TrackedDataHandler.ofEnum(MaidAttackType.class);
	}
}
