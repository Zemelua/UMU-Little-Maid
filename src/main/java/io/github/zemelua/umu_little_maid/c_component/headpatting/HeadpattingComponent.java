package io.github.zemelua.umu_little_maid.c_component.headpatting;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import io.github.zemelua.umu_little_maid.c_component.Components;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static net.fabricmc.api.EnvType.*;

public class HeadpattingComponent implements IHeadpattingComponent, AutoSyncedComponent {
	private final PlayerEntity owner;
	@Nullable private Entity target;

	/**
	 * モデルのレンダリング時に手の角度を計算するために使われます。
	 */
	@Environment(CLIENT) private int headpattingTicks;

	public HeadpattingComponent(PlayerEntity owner) {
		this.owner = owner;
	}

	@Override
	public void startHeadpatting(Entity target) {
		this.target = target;
		Components.HEADPATTING.sync(this.owner);
	}

	@Override
	public void finishHeadpatting() {
		this.target = null;
		Components.HEADPATTING.sync(this.owner);
	}

	@Override
	public Optional<Entity> getTarget() {
		return Optional.ofNullable(this.target);
	}

	@Override
	public void clientTick() {
		if (this.isHeadpatting()) {
			this.headpattingTicks++;
		} else {
			this.headpattingTicks = 0;
		}
	}

	@Environment(CLIENT)
	@Override
	public int getHeadpattingTicks() {
		return this.headpattingTicks;
	}

	@Override
	public void writeSyncPacket(PacketByteBuf packet, ServerPlayerEntity recipient) {
		packet.writeOptional(Optional.ofNullable(this.target).map(Entity::getId), PacketByteBuf::writeInt);
	}

	@Override
	public void applySyncPacket(PacketByteBuf packet) {
		this.target = packet.readOptional(PacketByteBuf::readInt)
				.map(i -> this.owner.getWorld().getEntityById(i))
				.orElse(null);
	}

	@Deprecated
	@Override
	public void readFromNbt(@NotNull NbtCompound nbt) {}

	@Deprecated
	@Override
	public void writeToNbt(@NotNull NbtCompound nbt) {}
}
