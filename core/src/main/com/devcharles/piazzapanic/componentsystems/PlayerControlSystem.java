package main.com.devcharles.piazzapanic.componentsystems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import main.com.devcharles.piazzapanic.components.B2dBodyComponent;
import main.com.devcharles.piazzapanic.components.ControllableComponent;
import main.com.devcharles.piazzapanic.components.PlayerComponent;
import main.com.devcharles.piazzapanic.components.Powerups.speedBoostComponent;
import main.com.devcharles.piazzapanic.input.KeyboardInput;
import main.com.devcharles.piazzapanic.utility.Mappers;

/**
 * Controls the one cook that has the PlayerComponent
 */
public class PlayerControlSystem extends IteratingSystem {    

    boolean hasInitComponent = false;

    KeyboardInput input;

    boolean changingCooks = false;
    PlayerComponent playerComponent;

    Engine engine;
    public PlayerControlSystem(KeyboardInput input, Engine engine) {
        super(Family.all(ControllableComponent.class).get());
        this.engine = engine;
        this.input = input;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Remember what playerComponent is, regardless of cook (just do this once at the start).
        if(!hasInitComponent) {
            this.playerComponent = Mappers.player.get(entity);

            // It doesn't matter if it gets run multiple times, just being explicit here.
            hasInitComponent = true;
        }

        // Collect toggleable inputs
        // Those need to be toggled off once recieved to prevent registering the input
        // twice
        if (this.changingCooks) {
            this.changingCooks = false;

            // [B1] Add playercomponent to new cook (the next cook iterated on by an external loop).
            entity.add(this.playerComponent);
        }

        if (!Mappers.player.has(entity)) {
            return;
        }

        if (input.changeCooks) {
            input.changeCooks = false;

            // This is run once for each chef in a given frame, so we set this now, and then the
            // value is caught by the next chef in the loop (ie on line 43,
            //if(this.changingCooks) {...} ).
            this.changingCooks = true;

            // [A1] Remove playercomponent from current cook.
            entity.remove(PlayerComponent.class);
            return;
        }

        if (input.changeCooksReverse) {
            input.changeCooksReverse = false;

            // [A2] Remove playercomponent from current cook.
            entity.remove(PlayerComponent.class);

            // Get an array of all the cooks and a record of how many cooks.
            ImmutableArray<Entity> cooks = engine.getEntitiesFor(getFamily());
            int cookNum = cooks.size();
            Entity prevCook = cooks.get((cooks.indexOf(entity, true)-1 + cookNum) % cookNum);

            // [B2] Add playercomponent to new cook (the previous cook identified above).
            prevCook.add(this.playerComponent);
            return;
        }

        if (input.putDown) {
            input.putDown = false;
            Mappers.player.get(entity).putDown = true;
        }
        if (input.pickUp) {
            input.pickUp = false;
            Mappers.player.get(entity).pickUp = true;
        }
        if (input.interact) {
            input.interact = false;
            Mappers.player.get(entity).interact = true;
        }
        if (input.compileMeal) {
            input.compileMeal = false;
            Mappers.player.get(entity).compileMeal = true;
        }
        if (input.giveToCustomer) {
            input.giveToCustomer = false;
            Mappers.player.get(entity).giveToCustomer = true;
        }

        B2dBodyComponent b2body = Mappers.b2body.get(entity);

        Vector2 direction = new Vector2(0, 0);

        // collect all the movement inputs
        if (input.left) {
            direction.add(-1, 0);
        }
        if (input.right) {
            direction.add(1, 0);
        }
        if (input.up) {
            direction.add(0, 1);
        }
        if (input.down) {
            direction.add(0, -1);
        }

        // Normalise vector (make length 1). This ensures player moves at the same speed
        // in all directions.
        // e.g. if player wants to go left and up at the same time, the vector is (1,1)
        // and length (speed) is sqrt(2)
        // but we need length to be 1
        direction.nor();
        int moveSpeed = 2000;
        if (entity.getComponent(speedBoostComponent.class) != null) {
            moveSpeed = speedBoostComponent.boostSpeed;
            if (entity.getComponent(speedBoostComponent.class).timeHad > speedBoostComponent.timeMax) {
                entity.remove(speedBoostComponent.class);
            } else {
                entity.getComponent(speedBoostComponent.class).timeHad += deltaTime;
            }
        }
        Vector2 finalV = direction.cpy().scl(moveSpeed * deltaTime);

        // Rotate the box2d shape in the movement direction
        if (!direction.isZero(0.7f)) {
            b2body.body.setTransform(b2body.body.getPosition(), direction.angleRad());
            b2body.body.applyLinearImpulse(finalV, b2body.body.getPosition(), true);
        }
    }

}
