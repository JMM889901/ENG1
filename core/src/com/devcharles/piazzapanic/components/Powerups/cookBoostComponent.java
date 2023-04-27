package com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Given to players while they are under the effect of a cook boost powerup.
 * <p>
 * <ul>
 * <li>boostTime (static, unchanging) is the new time it takes to cook.</li>
 * <li>timeMax (static, unchanging) is the maximum time the player can be under the effect of the boost.</li>
 * <li>timeHad (instance variable) is the time the player has been under the effect of the boost.</li>
 * </ul>
 * Values specified here {@link com.devcharles.piazzapanic.componentsystems.StationSystem#processStation(ControllableComponent, StationComponent) StationSystem.java}
 */
public class cookBoostComponent implements Component, Poolable {
    public static int boostTime = 2500; // How long cooking takes when boosted
    public static float timeMax = 5; //unimplemented
    public float timeHad; //unimplemented

    public void reset() {
        timeHad = 0;
    }
}
