package io.github.zemelua.umu_little_maid.unused;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import javax.annotation.Nullable;
import java.util.*;

public final class MaidCommand {
	public static final LiteralArgumentBuilder<ServerCommandSource> INSTANCE = CommandManager.literal("maid")
			.requires(source -> source.hasPermissionLevel(2))
			.then(CommandManager.literal("list")
					.executes(context -> MaidCommand.executeListDisplayCommand(context.getSource(), context.getSource().getPlayerOrThrow()))
					.then(CommandManager.argument("target", EntityArgumentType.player())
							.executes(context -> MaidCommand.executeListDisplayCommand(context.getSource(), EntityArgumentType.getPlayer(context, "target"))))
					.then(CommandManager.literal("clear")
							.executes(context -> MaidCommand.executeListClearCommand(Collections.singleton(context.getSource().getPlayerOrThrow())))
							.then(CommandManager.argument("targets", EntityArgumentType.players())
									.executes(context -> MaidCommand.executeListClearCommand(EntityArgumentType.getPlayers(context, "targets"))))))
			.then(CommandManager.literal("call")
					.then(CommandManager.argument("target", UuidArgumentType.uuid())
							.executes(context -> MaidCommand.executeCallCommand(context.getSource(), UuidArgumentType.getUuid(context, "target")))));

	private static int executeListDisplayCommand(ServerCommandSource source, ServerPlayerEntity player) {
		Map<UUID, NbtCompound> maids = MaidContracts.getMaids(player);

		if (!maids.isEmpty()) {
			for (Map.Entry<UUID, NbtCompound> maid : maids.entrySet()) {
				MutableText text = Texts.bracketed(Text.literal(maid.getKey().toString()).styled(style -> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, maid.getKey().toString())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.copy.click"))).withInsertion(maid.getKey().toString())));

				if (maid.getValue().contains("CustomName", NbtElement.STRING_TYPE)) {
					source.sendFeedback(Text.translatable("command.umu_little_maid.maid", maid.getValue().getString("CustomName"), text), true);
				} else {
					source.sendFeedback(Text.translatable("command.umu_little_maid.maid", "", text), true);
				}
			}
		}

		return 0;
	}

	private static int executeListClearCommand(Collection<ServerPlayerEntity> players) {
		for (ServerPlayerEntity player : players) {
			MaidContracts.getMaids(player).clear();
		}

		return 0;
	}

	private static int executeCallCommand(ServerCommandSource source, UUID id) {
		@Nullable Entity entity = source.getWorld().getEntity(id);
		if (entity instanceof LittleMaidEntity maid) {
			@Nullable Entity owner = maid.getOwner();
			if (owner != null){
				maid.teleport(maid.getOwner().getX(), maid.getY(), maid.getZ());

				return 0;
			}
		}

		Optional<PlayerEntity> owner = Optional.empty();
		Optional<UUID> uuid = Optional.empty();
		Optional<NbtCompound> nbt = Optional.empty();
		for (Map.Entry<PlayerEntity, Map<UUID, NbtCompound>> entry : MaidContracts.MAIDS.entrySet()) {
			for (Map.Entry<UUID, NbtCompound> entry1 : entry.getValue().entrySet()) {
				if (entry1.getKey().equals(id)) {
					owner = Optional.of(entry.getKey());
					uuid = Optional.of(entry1.getKey());
					nbt = Optional.of(entry1.getValue());

					break;
				}
			}
		}

		if (nbt.isPresent()) {
			@Nullable LittleMaidEntity maid = ModEntities.LITTLE_MAID
					.create(source.getWorld(), nbt.get(), null, null, owner.get().getBlockPos(), SpawnReason.COMMAND, false, false);
			if (maid != null) {
				maid.setUuid(uuid.get());
				maid.setOwner(owner.get());
				source.getWorld().spawnEntity(maid);
			}
		}

		return 0;
	}
}
