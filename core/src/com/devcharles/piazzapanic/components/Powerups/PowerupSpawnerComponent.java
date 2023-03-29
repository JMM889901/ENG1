package com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.devcharles.piazzapanic.utility.EntityFactory;

/**
 * Given to entities that spawn powerups.
 */
public class PowerupSpawnerComponent implements Component {
    public Vector2 pos;

    public Entity createPowerup(EntityFactory factory) {
        return factory.createPowerup(pos);
    }
}
