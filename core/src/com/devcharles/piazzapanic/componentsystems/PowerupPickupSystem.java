package com.devcharles.piazzapanic.componentsystems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.components.Powerups.PowerupComponent;
import com.devcharles.piazzapanic.components.Powerups.cookBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.cutBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.speedBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.PowerupComponent.powerupType;
import com.devcharles.piazzapanic.utility.Mappers;

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
            givePowerup(entity.getComponent(PowerupComponent.class).playerTouched,
                    entity.getComponent(PowerupComponent.class).type);
            destroyPowerup(entity);
        }
    }

    public void destroyPowerup(Entity entity) {
        world.destroyBody(Mappers.b2body.get(entity).body);
        engine.removeEntity(entity);
        // PowerupSpawnSystem.powerUps.remove(entity);
    }

    /**
     * What happens when a player walks over a powerup.
     * @param player
     * @param type
     */
    public void givePowerup(Entity player, powerupType type) {
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
            default:
                System.out.println("Tried to give a powerup that doesn't exist.");
                break;
        }
    }
}
