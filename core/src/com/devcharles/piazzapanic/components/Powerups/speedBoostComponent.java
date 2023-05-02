package com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Given to players while they are under the effect of a speed boost powerup.
 * <ul>
 * <li>boostSpeed (static, unchanging) is the new speed of the player.</li>
 * <li>timeMax (static, unchanging) is the maximum time the player can be under the effect of the boost.</li>
 * <li>timeHad (instance variable) is the time the player has been under the effect of the boost.</li>
 * </ul>
 * Used in implementation of FR_POWERUPS
 */
public class speedBoostComponent implements Component, Poolable {
    public static int boostSpeed = 4000;
    public static float timeMax = 5;
    public float timeHad = 0;

    public void reset() {
        timeHad = 0;
    }
}
