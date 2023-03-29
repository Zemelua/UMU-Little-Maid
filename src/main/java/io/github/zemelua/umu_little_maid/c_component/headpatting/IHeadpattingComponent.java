package io.github.zemelua.umu_little_maid.c_component.headpatting;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

import java.util.Optional;

import static net.fabricmc.api.EnvType.*;

public interface IHeadpattingComponent extends ComponentV3, ClientTickingComponent {
	void startHeadpatting(Entity target);
	void finishHeadpatting();
	Optional<Entity> getTarget();

	@Environment(CLIENT) int getHeadpattingTicks();

	default boolean isHeadpatting() {
		return this.getTarget().isPresent();
	}

	default boolean isHeadpattingWith(Entity entity) {
		if (this.getTarget().isPresent()) {
			return this.getTarget().get().equals(entity);
		}

		return false;
	}
}
