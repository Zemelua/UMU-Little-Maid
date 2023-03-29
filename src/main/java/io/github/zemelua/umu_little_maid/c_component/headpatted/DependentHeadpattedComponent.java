package io.github.zemelua.umu_little_maid.c_component.headpatted;

import io.github.zemelua.umu_little_maid.util.HeadpatManager;
import io.github.zemelua.umu_little_maid.util.IHasMaster;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import static net.fabricmc.api.EnvType.*;

public class DependentHeadpattedComponent<T extends Entity & IHasMaster> implements IHeadpattedComponent {
	private final T owner;

	@Environment(CLIENT) private int headpattingTicks;

	public DependentHeadpattedComponent(T owner) {
		this.owner = owner;
	}

	@Override
	public boolean isHeadpatted() {
		if (this.owner.getMaster().isPresent()) {
			return HeadpatManager.isHeadpattingWith(this.owner.getMaster().get(), this.owner);
		}

		return false;
	}

	@Override
	public int getHeadpattedTicks() {
		return this.headpattingTicks;
	}

	@Override
	public void clientTick() {
		if (this.isHeadpatted()) {
			this.headpattingTicks++;
		} else {
			this.headpattingTicks = 0;
		}
	}

	@Override public void readFromNbt(@NotNull NbtCompound nbt) {}
	@Override public void writeToNbt(@NotNull NbtCompound nbt) {}
}
