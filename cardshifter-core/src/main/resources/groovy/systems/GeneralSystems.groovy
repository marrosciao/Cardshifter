package systems;

import SystemsDelegate
import com.cardshifter.modapi.actions.UseCostSystem
import com.cardshifter.modapi.actions.attack.AttackDamageAccumulating
import com.cardshifter.modapi.actions.attack.AttackDamageHealAtEndOfTurn
import com.cardshifter.modapi.actions.attack.AttackOnBattlefield
import com.cardshifter.modapi.actions.attack.AttackSickness
import com.cardshifter.modapi.actions.attack.AttackTargetMinionsFirstThenPlayer
import com.cardshifter.modapi.actions.attack.TrampleSystem
import com.cardshifter.modapi.base.ECSGame
import com.cardshifter.modapi.base.ECSSystem
import com.cardshifter.modapi.base.Entity
import com.cardshifter.modapi.cards.DamageConstantWhenOutOfCardsSystem
import com.cardshifter.modapi.cards.DrawCardAtBeginningOfTurnSystem
import com.cardshifter.modapi.cards.DrawCardEvent
import com.cardshifter.modapi.cards.DrawStartCards
import com.cardshifter.modapi.cards.LimitedHandSizeSystem
import com.cardshifter.modapi.cards.PlayEntersBattlefieldSystem
import com.cardshifter.modapi.cards.PlayFromHandSystem
import com.cardshifter.modapi.phase.GainResourceSystem
import com.cardshifter.modapi.phase.PerformerMustBeCurrentPlayer
import com.cardshifter.modapi.phase.RestoreResourcesSystem
import com.cardshifter.modapi.players.Players
import com.cardshifter.modapi.resources.ECSResource
import com.cardshifter.modapi.resources.RestoreResourcesToSystem
import net.zomis.cardshifter.ecs.effects.EntityInt
import net.zomis.cardshifter.ecs.usage.ApplyAfterAttack
import net.zomis.cardshifter.ecs.usage.DestroyAfterUseSystem

import java.util.function.BiPredicate
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.function.ToIntFunction
import java.util.function.UnaryOperator;

class AttackSystemDelegate {
    ECSGame game

    def zone(String name) {
        assert name == 'Battlefield' // Only supported right now
        addSystem(new AttackOnBattlefield())
    }

    def cardsFirst(ECSResource resource) {
        addSystem new AttackTargetMinionsFirstThenPlayer(resource)
    }

    def sickness(ECSResource resource) {
        addSystem new AttackSickness(resource)
    }

    def accumulating(ECSResource attack, ECSResource health, BiPredicate<Entity, Entity> allowCounterAttack) {
        addSystem new AttackDamageAccumulating(attack, health, allowCounterAttack)
    }

    def healAtEndOfTurn(ECSResource health, ECSResource maxHealth) {
        addSystem new AttackDamageHealAtEndOfTurn(health, maxHealth)
    }

    def afterAttack(Predicate<Entity> condition, Consumer<Entity> apply) {
        addSystem new ApplyAfterAttack(condition, apply)
    }

    def trample(ECSResource resource) {
        addSystem new TrampleSystem(resource)
    }

    def methodMissing(String name, args) {
        println 'AttackSystems missing method ' + name
    }

    def addSystem(ECSSystem system) {
        game.addSystem(system)
    }
}

public class GeneralSystems {
    static UnaryOperator<Entity> whoPays(String str) {
        if (str.equals('owner') || str.equals('player')) {
            println 'return owner pays'
            return {Entity e -> Players.findOwnerFor(e)}
        }
        if (str.equals('self')) {
            println 'return self pays'
            return {Entity e -> e}
        }
        println 'neither matches. throw exception'
        throw new UnsupportedOperationException('whoPays? ' + str)
    }

    static def setup(ECSGame game) {
        SystemsDelegate.metaClass.PerformerMustBeCurrentPlayer << {
            addSystem(new PerformerMustBeCurrentPlayer())
        }
        SystemsDelegate.metaClass.startCards << {int count ->
            addSystem(new DrawStartCards(count))
        }
        SystemsDelegate.metaClass.attackSystem << {Closure clos ->
            clos.delegate = new AttackSystemDelegate(game: game)
            clos.call()
        }
    }

    static def resourceSystems() {
        SystemsDelegate.metaClass.gainResource << {Map map ->
            ECSResource resource = (ECSResource) map.get('res')
            int value = (int) map.get('value')
            int untilMax = (int) map.get('untilMax')
            EntityInt restore = {e -> Math.min(1, Math.max((int) 0, (int) untilMax - resource.getFor(entity)))}

            addSystem(new GainResourceSystem(resource, {e -> 1}))
        }
        SystemsDelegate.metaClass.restoreResources << {Map map ->
            ECSResource resource = (ECSResource) map.get('resource')
            Object value = map.get('value')
            EntityInt entityInt;
            if (value instanceof Closure) {
                entityInt = value
            } else if (value instanceof ECSResource) {
                entityInt = {Entity e -> (value as ECSResource).getFor(e)}
            } else {
                assert value instanceof Number
                entityInt = {e -> value}
            }
            assert resource
            assert entityInt
            addSystem new RestoreResourcesSystem(resource, entityInt)
        }
        SystemsDelegate.metaClass.useCost << {Map map ->
//            (action: PLAY_ACTION, res: MANA, value: { res MANA_COST }, whoPays: "player")
            String action = map.get('action')
            ECSResource res = (ECSResource) map.get('res')
            Object value = map.get('value')
            String whoPaysStr = map.get('whoPays')
            ToIntFunction<Entity> entityInt = value instanceof ECSResource ?
                    {Entity e -> (value as ECSResource).getFor(e)} :
                    {Entity e -> (value as int).intValue()}
            UnaryOperator<Entity> whoPays = whoPays(whoPaysStr)
            assert whoPays : 'Who pays is null: ' + whoPaysStr
            addSystem new UseCostSystem(action, res, entityInt, whoPays)
        }
        SystemsDelegate.metaClass.RestoreResourcesToSystem << {Map map ->
            Predicate<Entity> filter = map.get('filter') as Predicate<Entity>
            ECSResource resource = map.get('resource') as ECSResource
            Object value = map.get('value')
            ToIntFunction<Entity> entityInt
            if (value instanceof Closure) {
                entityInt = value as ToIntFunction<Entity>
            } else {
                assert value instanceof Number
                entityInt = {e -> value}
            }
            addSystem new RestoreResourcesToSystem(filter, resource, entityInt)
        }
    }

    static def cardSystems() {
        SystemsDelegate.metaClass.playFromHand << {String zone ->
            addSystem(new PlayFromHandSystem(zone))
        }
        SystemsDelegate.metaClass.playEntersBattlefield << {String action ->
            addSystem(new PlayEntersBattlefieldSystem(action))
        }
        SystemsDelegate.metaClass.destroyAfterUse << {String action ->
            addSystem(new DestroyAfterUseSystem(action))
        }
        SystemsDelegate.metaClass.DrawCardAtBeginningOfTurnSystem << {
            addSystem new DrawCardAtBeginningOfTurnSystem()
        }
        SystemsDelegate.metaClass.DamageConstantWhenOutOfCardsSystem << {ECSResource resource, int count ->
            addSystem new DamageConstantWhenOutOfCardsSystem(resource, count)
        }
        SystemsDelegate.metaClass.LimitedHandSizeSystem << {int limit, Consumer<DrawCardEvent> whenFull ->
            addSystem new LimitedHandSizeSystem(limit, whenFull)
        }
    }
}
