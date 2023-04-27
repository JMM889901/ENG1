package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Component give to anything that wants to be animated.
 */
public class AnimationComponent implements Component {
    public Animation<TextureRegion> animation = null;
}
