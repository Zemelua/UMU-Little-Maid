package io.github.zemelua.umu_little_maid.c_component.headpatted;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import net.fabricmc.api.Environment;

import static net.fabricmc.api.EnvType.*;

public interface IHeadpattedComponent extends ComponentV3, ClientTickingComponent {
	boolean isHeadpatted();

	@Environment(CLIENT) int getHeadpattedTicks();
}
