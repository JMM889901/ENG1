package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * All entities will have this because it's assumed all entities want to be rendered.
 * 
 * This component stores xy scale and the texture (or such an area within) to be drawn.
 */
public class TextureComponent implements Component {
    public TextureRegion region = null;
    public Vector2 scale = new Vector2(1,1);
}
