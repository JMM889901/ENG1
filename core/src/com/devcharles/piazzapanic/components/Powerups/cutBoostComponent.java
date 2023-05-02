package com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Given to players while they are under the effect of a cut boost powerup.
 * Used in implementation of FR_POWERUPS
 */
public class cutBoostComponent implements Component, Poolable {
    public static int boostTime = 4000; // How long cutting takes when boosted (I believe this is in milliseconds).
    public static float timeMax = 5;
    public float timeHad = 0;

    public void reset() {
        timeHad = 0;
    }
}
