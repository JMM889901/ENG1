package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import com.devcharles.piazzapanic.utility.WalkAnimator;

/**
 * Component for things that walk (i.e. chefs and customers).
 */
public class WalkingAnimationComponent implements Component{
    public WalkAnimator animator;  // This class works out directions etc. as part of animation.
}
