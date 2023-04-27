package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * This gives a tint to an entity, this is only used on food that has been processed on a station.
 */
public class TintComponent implements Component {
    public Color tint;
}
