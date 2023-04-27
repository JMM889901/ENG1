package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import com.devcharles.piazzapanic.utility.box2d.Box2dSteeringBody;

/**
 * AI movement component given to customer entities.
 */
public class AIAgentComponent implements Component {
    public Box2dSteeringBody steeringBody;
    public int currentObjective = 0;  // I think this stores where in the queue of customers the "AI" thinks it is? - Joss
}