package io.github.zemelua.umu_little_maid.c_component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class InstructionComponent implements IInstructionComponent, AutoSyncedComponent {
	private final PlayerEntity owner;

	private boolean instructing;
	@Nullable private Entity target;

	public InstructionComponent(PlayerEntity owner) {
		this.owner = owner;
	}

	@Override
	public void startInstruction(Entity target) {
		this.instructing = true;
		this.target = target;
	}

	@Override
	public void finishInstruction() {
		this.instructing = false;
		this.target = null;
	}

	@Override
	public boolean isInstructing() {
		return this.instructing;
	}

	@Override
	public Optional<Entity> getTarget() {
		return Optional.ofNullable(this.target);
	}

	@Override
	public void writeSyncPacket(PacketByteBuf packet, ServerPlayerEntity recipient) {
		packet.writeBoolean(this.instructing);
		packet.writeOptional(Optional.ofNullable(this.target)
				.map(Entity::getId), PacketByteBuf::writeInt);
	}

	@Override
	public void applySyncPacket(PacketByteBuf packet) {
		this.instructing = packet.readBoolean();
		this.target = packet.readOptional(PacketByteBuf::readInt)
				.map(v -> this.owner.getWorld().getEntityById(v))
				.orElse(null);
	}

	@Override
	public void readFromNbt(@NotNull NbtCompound nbt) {
		// セーブデータに保存しない
	}

	@Override
	public void writeToNbt(@NotNull NbtCompound nbt) {
		// セーブデータから読み込まない
	}
}
