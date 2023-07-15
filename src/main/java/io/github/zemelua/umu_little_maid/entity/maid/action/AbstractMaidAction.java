package io.github.zemelua.umu_little_maid.entity.maid.action;

public abstract class AbstractMaidAction implements IMaidAction {
	private final int length;
	private final IMaidAction.Type type;

	private int ticks;

	public AbstractMaidAction(int length, IMaidAction.Type type) {
		this.length = length;
		this.type = type;
	}

	protected abstract void tick(int ticks);

	@Override
	public boolean tick() {
		this.ticks++;

		this.tick(this.ticks);

		return this.ticks >= this.length;
	}

	@Override
	public Type getType() {
		return this.type;
	}
}
