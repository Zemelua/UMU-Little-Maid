package io.github.zemelua.umu_little_maid.entity.maid;

import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MaidNavigation extends MobNavigation {
	public MaidNavigation(LittleMaidEntity maid, World world) {
		super(maid, world);
	}

	@Override
	protected void adjustPath() {
		super.adjustPath();

		if (this.currentPath == null) return;

		if (this.entity.isBeingRainedOn() && ((LittleMaidEntity) this.entity).shouldAvoidRain()) {
			for (int i = 0; i < this.currentPath.getLength(); i++) {
				PathNode node = this.currentPath.getNode(i);
				if (this.world.hasRain(new BlockPos(node.x, node.y, node.z))
						|| this.world.hasRain(BlockPos.ofFloored(node.x, node.y + this.entity.getBoundingBox().getMax(Direction.Axis.Y), node.z))) {
					this.currentPath.setLength(i);

					return;
				}
			}
		}
	}
}
