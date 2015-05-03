var pgres = Java.type("net.zomis.cardshifter.ecs.usage.PhrancisGame").PhrancisResources;
var noAttackCreatures = [];
var keywords = {};
keywords.cards = {};
keywords.cards.name = function (entity, obj, value) {
    print("before apply: " + entity);
    com.cardshifter.modapi.attributes.ECSAttributeMap.createOrGetFor(entity).set(com.cardshifter.modapi.attributes.Attributes.NAME, value);
    print("after apply");
}
keywords.cards.flavor = function (entity, obj, value) {
    com.cardshifter.modapi.attributes.ECSAttributeMap.createOrGetFor(entity).set(com.cardshifter.modapi.attributes.Attributes.FLAVOR, value);
}
keywords.cards.creature = function (entity, obj, value) {
    var pg = Java.type("net.zomis.cardshifter.ecs.usage.PhrancisGame");
    var actions = new com.cardshifter.modapi.actions.ActionComponent();
	entity.addComponent(actions);

	actions.addAction(pg.playAction(entity));
	actions.addAction(pg.attackAction(entity));
	actions.addAction(pg.scrapAction(entity));

	entity.addComponent(new com.cardshifter.modapi.base.CreatureTypeComponent(value));

	var map = com.cardshifter.modapi.resources.ECSResourceMap.createOrGetFor(entity);
	map.set(pg.PhrancisResources.SICKNESS, 1);
	map.set(pg.PhrancisResources.TAUNT, 1);
	map.set(pg.PhrancisResources.ATTACK_AVAILABLE, 1);

}

keywords.cards.manaCost = function (entity, obj, value) {
	com.cardshifter.modapi.resources.ECSResourceMap.createOrGetFor(entity).set(pgres.MANA_COST, value);
}

keywords.cards.health = function (entity, obj, value) {
    if (!obj.creature) {
        throw new Error("expected creature");
    }
	com.cardshifter.modapi.resources.ECSResourceMap.createOrGetFor(entity).set(pgres.HEALTH, value);
	com.cardshifter.modapi.resources.ECSResourceMap.createOrGetFor(entity).set(pgres.MAX_HEALTH, value);
}

keywords.cards.attack = function (entity, obj, value) {
    if (!obj.creature) {
        throw new Error("expected creature");
    }
	com.cardshifter.modapi.resources.ECSResourceMap.createOrGetFor(entity).set(pgres.ATTACK, value);
}

keywords.cards.scrap = function (entity, obj, value) {
    if (!obj.creature) {
        throw new Error("expected creature");
    }
	com.cardshifter.modapi.resources.ECSResourceMap.createOrGetFor(entity).set(pgres.SCRAP, value);
}

keywords.cards.sickness = function (entity, obj, value) {
    if (!obj.creature) {
        throw new Error("expected creature");
    }
	com.cardshifter.modapi.resources.ECSResourceMap.createOrGetFor(entity).set(pgres.SICKNESS, value);
}

keywords.cards.noAttack = function (entity, obj, value) {
    if (!obj.creature) {
        throw new Error("expected creature");
    }
	noAttackCreatures.push(obj.name);
}

keywords.cards.denyCounterAttack = function (entity, obj, value) {
    if (!obj.creature) {
        throw new Error("expected creature");
    }
	com.cardshifter.modapi.resources.ECSResourceMap.createOrGetFor(entity).set(pgres.DENY_COUNTERATTACK, value);
}

keywords.cards.enchantment = function (entity, obj, value) {
    if (obj.creature) {
        throw new Error("cannot be both enchantment and creature at once");
    }
    var actions = new com.cardshifter.modapi.actions.ActionComponent();
    var pg = Java.type("net.zomis.cardshifter.ecs.usage.PhrancisGame");
    actions.addAction(pg.enchantAction(entity));
	entity.addComponent(actions);
}

keywords.cards.scrapCost = function (entity, obj, value) {
    if (!obj.enchantment) {
        throw new Error("expected enchantment");
    }
	com.cardshifter.modapi.resources.ECSResourceMap.createOrGetFor(entity).set(pgres.SCRAP_COST, value);
}

keywords.cards.addAttack = function (entity, obj, value) {
    if (!obj.enchantment) {
        throw new Error("expected enchantment");
    }
	com.cardshifter.modapi.resources.ECSResourceMap.createOrGetFor(entity).set(pgres.ATTACK, value);
}

keywords.cards.addHealth = function (entity, obj, value) {
    if (!obj.enchantment) {
        throw new Error("expected enchantment");
    }
	com.cardshifter.modapi.resources.ECSResourceMap.createOrGetFor(entity).set(pgres.HEALTH, value);
	com.cardshifter.modapi.resources.ECSResourceMap.createOrGetFor(entity).set(pgres.MAX_HEALTH, value);
}

function applyEntity(game, card, entity, keyword) {
    print("applyEntity " + card + ": " + entity);

    for (var property in card) {
        if (card.hasOwnProperty(property)) {
            var value = card[property];
            print("property found: " + property + " with value " + value + " keyword data is " + keyword[property]);
            if (keyword[property] === undefined) {
                print("keyword " + property + " is undefined");
                throw new Error("property " + property + " was found but is not a declared keyword");
            }
            keyword[property].call(null, entity, card, value);
        }
    }

}


function applyCardKeywords(game, zone, data) {

    for (var i = 0; i < data.cards.length; i++) {
        var card = data.cards[i];
        var entity = game.newEntity();
        print("entity is " + entity);
        applyEntity(game, card, entity, keywords.cards);
        zone.addOnBottom(entity);
    }
}

function applyKeywords(game, data) {

    beforeApplyKeywords(game, data);
    applyKeywords(game, data);
    afterApplyKeywords(game, data);

}