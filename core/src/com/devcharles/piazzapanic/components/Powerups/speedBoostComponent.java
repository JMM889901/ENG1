package com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Given to players while they are under the effect of a speed boost powerup.
 * Searched for in {@link componentsystems.PlayerControlSystem}
 * Given in {@link componentsystems.PowerupSpawnSystem}
 */
public class speedBoostComponent implements Component, Poolable {
    public static int boostSpeed = 4000;
    public static float timeMax = 5;
    public float timeHad = 0;

    public void reset() {
        timeHad = 0;
    }
}
