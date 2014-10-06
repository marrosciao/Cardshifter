package net.zomis.cardshifter.ecs.cards;

import java.util.function.Consumer;

import net.zomis.cardshifter.ecs.base.ECSGame;
import net.zomis.cardshifter.ecs.base.ECSSystem;

public class LimitedHandSizeSystem implements ECSSystem {

	private final int limit;
	private final Consumer<DrawCardEvent> actionWhenFull;

	public LimitedHandSizeSystem(int limit, Consumer<DrawCardEvent> actionWhenFull) {
		this.limit = limit;
		this.actionWhenFull = actionWhenFull;
	}

	@Override
	public void startGame(ECSGame game) {
		game.getEvents().registerHandlerBefore(this, DrawCardEvent.class, this::drawCard);
	}
	
	private void drawCard(DrawCardEvent event) {
		if (event.getToZone().size() >= limit) {
			event.setCancelled(true);
			actionWhenFull.accept(event);
		}
	}

	@Override
	public String toString() {
		return "LimitedHandSizeSystem [limit=" + limit + ", actionWhenFull="
				+ actionWhenFull + "]";
	}
	
}
