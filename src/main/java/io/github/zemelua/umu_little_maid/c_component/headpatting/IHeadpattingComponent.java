package io.github.zemelua.umu_little_maid.c_component.headpatting;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;

import java.util.Optional;

public interface IHeadpattingComponent extends ComponentV3 {
	void startHeadpatting(LittleMaidEntity target);
	void finishHeadpatting();
	Optional<LittleMaidEntity> getTarget();

	default boolean isHeadpatting() {
		return this.getTarget().isPresent();
	}
}
