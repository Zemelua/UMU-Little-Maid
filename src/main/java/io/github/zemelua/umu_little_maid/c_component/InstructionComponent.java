package io.github.zemelua.umu_little_maid.c_component;

import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.InstructionUtils;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class InstructionComponent implements IInstructionComponent, AutoSyncedComponent {
	private final PlayerEntity owner;
	@Nullable private LittleMaidEntity target;

	public InstructionComponent(PlayerEntity owner) {
		this.owner = owner;
	}

	@Override
	public void startInstruction(LittleMaidEntity target) {
		this.target = target;

		Components.INSTRUCTION.sync(this.owner);
	}

	@Override
	public ActionResult tryInstruction(World world, BlockHitResult target) {
		Objects.requireNonNull(this.target);
		BlockPos pos = target.getBlockPos();
		BlockState state = world.getBlockState(pos);
		Optional<GlobalPos> home = this.target.getHome();
		Collection<GlobalPos> boxes = this.target.getDeliveryBoxes();
		Optional<GlobalPos> sameBox = boxes.stream().filter(b -> ModUtils.isSameObject(world, pos, b)).findAny();

		if (home.isPresent() && ModUtils.isSameObject(world, pos, home.get())) {
			if (!world.isClient()) {
				this.target.removeHome();
				this.finishInstruction();

				this.owner.sendMessage(InstructionUtils.removeHomeMessage());
			}

			return ActionResult.success(world.isClient());
		} else if (sameBox.isPresent()) {
			if (!world.isClient()) {
				this.target.removeDeliveryBox(sameBox.get());
				this.finishInstruction();

				this.owner.sendMessage(InstructionUtils.removeDeliveryBoxMessage(state.getBlock(), pos));
			}

			return ActionResult.success(world.isClient());
		} else if (this.target.isSettableAsHome(world, pos)) {
			if (!world.isClient()) {
				this.target.setHome(GlobalPos.create(world.getRegistryKey(), pos));
				this.finishInstruction();

				if (home.isPresent()) {
					this.owner.sendMessage(InstructionUtils.replaceHomeMessage(state.getBlock(), pos));
				} else {
					this.owner.sendMessage(InstructionUtils.setHomeMessage(state.getBlock(), pos));
				}
			}

			return ActionResult.success(world.isClient());
		} else if (this.target.isSettableAsDeliveryBox(world, pos)) {
			if (!world.isClient()) {
				this.target.addDeliveryBox(GlobalPos.create(world.getRegistryKey(), pos));
				this.finishInstruction();

				this.owner.sendMessage(InstructionUtils.addDeliveryBoxMessage(state.getBlock(), pos));
			}

			return ActionResult.success(world.isClient());
		} else {
			if (world.isClient()) {
				this.owner.sendMessage(InstructionUtils.passOnBlockMessage(), true);
			}

			return ActionResult.FAIL;
		}
	}

	@Override
	public ActionResult tryInstruction(World world, EntityHitResult target) {
		if (world.isClient()) {
			this.owner.sendMessage(InstructionUtils.passOnEntityMessage(), true);
		}

		return ActionResult.FAIL;
	}

	@Override
	public void finishInstruction() {
		this.target = null;

		Components.INSTRUCTION.sync(this.owner);
	}

	@Override
	public void cancelInstruction() {
		this.target = null;

		Components.INSTRUCTION.sync(this.owner);

		if (!this.owner.getWorld().isClient()) {
			this.owner.sendMessage(InstructionUtils.cancelMessage(), true);
		}
	}

	@Override
	public Optional<LittleMaidEntity> getTarget() {
		return Optional.ofNullable(this.target);
	}

	/**
	 * CCの仕様上、 {@link ComponentRegistryV3#getOrCreate(Identifier, Class)} のパラメータクラスにティックインターフェー
	 * スが含まれている必要があるため {@link IInstructionComponent} から {@link ServerTickingComponent#serverTick()} を呼
	 * び出すことができますが、CCによって自動で毎ティック実行されるため、他の場所で呼び出さないでください。
	 */
	@Deprecated
	@Override
	public void serverTick() {
		if (this.isInstructing()) {
			ItemStack mainHandStack = this.owner.getMainHandStack();
			ItemStack offHandStack = this.owner.getOffHandStack();

			if (!mainHandStack.isIn(ModTags.ITEM_MAID_INSTRUCTORS) && !offHandStack.isIn(ModTags.ITEM_MAID_INSTRUCTORS)) {
				this.cancelInstruction();
			}
		}
	}

	@Override
	public void writeSyncPacket(PacketByteBuf packet, ServerPlayerEntity recipient) {
		packet.writeOptional(Optional.ofNullable(this.target)
				.map(Entity::getId), PacketByteBuf::writeInt);
	}

	@Override
	public void applySyncPacket(PacketByteBuf packet) {
		this.target = (LittleMaidEntity) packet.readOptional(PacketByteBuf::readInt)
				.map(i -> this.owner.getWorld().getEntityById(i))
				.filter(e -> e instanceof LittleMaidEntity)
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
