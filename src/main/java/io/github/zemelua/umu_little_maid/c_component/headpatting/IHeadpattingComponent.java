package io.github.zemelua.umu_little_maid.c_component.headpatting;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.fabricmc.api.Environment;

import java.util.Optional;

import static net.fabricmc.api.EnvType.*;

public interface IHeadpattingComponent extends ComponentV3, ClientTickingComponent {
	void startHeadpatting(LittleMaidEntity target);
	void finishHeadpatting();
	Optional<LittleMaidEntity> getTarget();

	@Environment(CLIENT) int getHeadpattingTicks();

	default boolean isHeadpatting() {
		return this.getTarget().isPresent();
	}
}
