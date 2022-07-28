package io.github.zemelua.umu_little_maid.entity.control;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;

public class MaidControl extends MoveControl {
	public MaidControl(LittleMaidEntity maid) {
		super(maid);
	}

	@Override
	public void tick() {
		LittleMaidEntity maid = (LittleMaidEntity) this.entity;
		maid.setSprinting(this.state == State.MOVE_TO);

		if (this.state == State.MOVE_TO && maid.canSwim()) {
			this.entity.setVelocity(this.entity.getVelocity().add(0.0, 0.005, 0.0));

			double x = this.targetX - this.entity.getX();
			double y = this.targetY - this.entity.getY();
			double z = this.targetZ - this.entity.getZ();
			if (x * x + y * y + z * z < 2.500000277905201E-7) {
				this.entity.setForwardSpeed(0.0f);

				return;
			}

			float yaw = (float) Math.toDegrees(MathHelper.atan2(z, x)) - 90.0F;
			this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), yaw, 10));
			this.entity.bodyYaw = this.entity.getYaw();
			this.entity.headYaw = this.entity.getYaw();

			double length = Math.sqrt(x * x + z * z);
			if (Math.abs(y) > 1.0E-5D || Math.abs(length) > 1.0E-5D) {
				float pitch = (float) -Math.toDegrees(MathHelper.atan2(y, length));
				pitch = MathHelper.clamp(MathHelper.wrapDegrees(pitch), -85.0F, 85.0F);
				this.entity.setPitch(this.wrapDegrees(this.entity.getPitch(), pitch, 5.0F));
			}

			float speed = (float) (this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * this.speed * 100.0F);
			float forward = MathHelper.cos((float) Math.toRadians(this.entity.getPitch()));
			float upward = -MathHelper.sin((float) Math.toRadians(this.entity.getPitch()));
			this.entity.setMovementSpeed(speed);
			this.entity.setForwardSpeed(forward * speed);
			this.entity.setUpwardSpeed(upward * speed);
		} else {
			super.tick();
		}

		if (this.state == State.WAIT) {

		}
	}
}
