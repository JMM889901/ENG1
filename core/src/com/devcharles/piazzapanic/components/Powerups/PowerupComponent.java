package com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * Component given to entities that are powerups, which get dotted around the kitchen.
 * <p>
 * It records what the powerup type is (set in {@link com.devcharles.piazzapanic.componentsystems.PowerupSpawnSystem PowerupSpawnSystem.java}) in a variable.
 * <p>
 * It also records which player touched it (set by the entity collision system, to supply the data
 * to the powerup pickup system).
 */
public class PowerupComponent implements Component {
    public enum powerupType {
        speedBoost,
        cookBoost,
        cutBoost,
        timeFreezeBoost,
        orderBoost
    }

    public Entity playerTouched;
    public powerupType type;
    public boolean markedForDeletion;
}
