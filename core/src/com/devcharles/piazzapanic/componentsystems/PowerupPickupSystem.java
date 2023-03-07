package com.devcharles.piazzapanic.componentsystems;

import java.util.ArrayList;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.components.CustomerComponent;
import com.devcharles.piazzapanic.components.Powerups.PowerupComponent;
import com.devcharles.piazzapanic.components.Powerups.cookBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.cutBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.speedBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.timeFreezeBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.PowerupComponent.powerupType;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;

public class PowerupPickupSystem extends IteratingSystem {
    World world;
    Engine engine;

    public PowerupPickupSystem(Engine engine, World world) {
        super(Family.all(PowerupComponent.class).get());
        this.world = world;
        this.engine = engine;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.getComponent(PowerupComponent.class).markedForDeletion == true) {
            if (givePowerup(entity.getComponent(PowerupComponent.class).playerTouched,
                    entity.getComponent(PowerupComponent.class).type)) {
                destroyPowerup(entity);
            }
        }
    }

    public void destroyPowerup(Entity entity) {
        world.destroyBody(Mappers.b2body.get(entity).body);
        engine.removeEntity(entity);
        // PowerupSpawnSystem.powerUps.remove(entity);
    }

    /**
     * What happens when a player walks over a powerup.
     * <<<<<<< HEAD
     * 
     * @param player
     * @param type
     *               =======
     * @param player The chef/entity being controlled by the user.
     * @param type   The type of powerup picked up (eg cook boost, time freeze
     *               etc.).
     * @return Whether the powerup was used.
     *         >>>>>>> 3f4b4aec75666f83d7dbd5b21e30908f1f45328d
     */
    public boolean givePowerup(Entity player, powerupType type) {
        System.out.print("Given powerup: ");
        System.out.println(type);

        switch (type) {
            case speedBoost:
                // "Status effects" such as speed or cook boost are recorded as components.
                speedBoostComponent component = engine.createComponent(speedBoostComponent.class);
                player.add(component);
                break;
            case cookBoost:
                player.add(engine.createComponent(cookBoostComponent.class));
                break;
            case cutBoost:
                player.add(engine.createComponent(cutBoostComponent.class));
                break;
            case timeFreezeBoost:
                // Stuff can be changed in HUD.java for this.
                player.add(engine.createComponent(timeFreezeBoostComponent.class));
                break;
            case orderBoost: // Fulfil the order immediately
                ImmutableArray<Entity> customers = engine.getEntitiesFor(Family.all(CustomerComponent.class).get());
                // Gets all the entities that have CustomerComponent
                ArrayList<Entity> filtered_customers = new ArrayList<Entity>(); // filter out customers
                // that have been served (because trying to auto serve them will just crash).

                for (Entity customer : customers) {
                    if (customer.getComponent(CustomerComponent.class).order != null) {
                        filtered_customers.add(customer);
                    }
                }

                // If there are customers, choose one at random.
                if (filtered_customers.size() > 0) {
                    Entity happyCustomer = filtered_customers.get((int) (Math.random() * filtered_customers.size()));
                    engine.getSystem(CustomerAISystem.class).autoFulfillOrder(happyCustomer);
                } else {
                    // Otherwise, let the calling function know that the powerup wasn't used.
                    return false;
                }

                break;
            default:
                System.out.println("(!) Tried to give a powerup that doesn't exist.");
                break;
        }

        return true; // Assume the powerup is used if not otherwise indicated above.
    }
}