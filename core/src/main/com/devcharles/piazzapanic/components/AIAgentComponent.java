package main.com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import main.com.devcharles.piazzapanic.utility.box2d.Box2dSteeringBody;

public class AIAgentComponent implements Component {
    public Box2dSteeringBody steeringBody;
    public int currentObjective = 0;
}