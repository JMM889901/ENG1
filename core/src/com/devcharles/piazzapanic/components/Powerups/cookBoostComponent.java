package com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Given to players while they are under the effect of a cook boost powerup.
 * Searched for in {@link componentsystems.StationSystem}
 * <p>
 * Given in {@link componentsystems.PowerupSpawnSystem}
 */
public class cookBoostComponent implements Component, Poolable {
    public static int boostTime = 2500; // How long cooking takes when boosted
    public static float timeMax = 5;
    public float timeHad;

    public void reset() {
        timeHad = 0;
    }
}
