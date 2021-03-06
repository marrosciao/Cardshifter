package com.cardshifter.ai;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.cardshifter.modapi.actions.Actions;
import com.cardshifter.modapi.actions.ECSAction;
import com.cardshifter.modapi.actions.TargetSet;
import com.cardshifter.modapi.ai.CardshifterAI;
import com.cardshifter.modapi.base.Entity;

public class CompleteIdiot implements CardshifterAI {

	private static final Logger logger = LogManager.getLogger(CompleteIdiot.class);
	
	private final Random random = new Random();
	
	@Override
	public ECSAction getAction(Entity player) {
		
		Stream<ECSAction> actions = Actions.getPossibleActionsFor(player).stream();
		
		Stream<ECSAction> allActions = actions
				.filter(action -> setTargetIfPossible(action));
		List<ECSAction> list = allActions.collect(Collectors.toList());
		logger.info(player + " allowed actions: " + list);
		
		//return nothing if no actions are available
		if (list.isEmpty()) {
			return null;
		}

		//Do not scrap if it is the only thing you can do
		if (list.size() == 1) {
			for (ECSAction action : list) {
				if(action.getName().equals("Scrap")) {
					return null;
				}
			}
		}

		//parse the actions and return an appropriate one based on the actions available
		//For example, if there are less than 3 creatures on the board, do not scrap any
		//If any attacks are available, do those

		//return a random action from the list
		return list.get(random.nextInt(list.size()));
	}

	private boolean setTargetIfPossible(ECSAction action) {
		for (TargetSet targetset : action.getTargetSets()) {
			targetset.clearTargets();
			while (!targetset.hasEnoughTargets()) {
				List<Entity> targets = targetset.findPossibleTargets();
				if (targets.isEmpty()) {
					return false;
				}
				targetset.addTarget(targets.get(random.nextInt(targets.size())));
			}
			return true;
		}
		return true;
	}

}
