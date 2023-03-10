package main.com.devcharles.piazzapanic.components;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import main.com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import main.com.devcharles.piazzapanic.utility.Station.StationType;
import main.com.devcharles.piazzapanic.utility.Station.itemDisplayDir;

public class StationComponent implements Component {
    public Entity interactingCook = null;
    public StationType type;
    public itemDisplayDir direction;
    public ArrayList<Entity> food = new ArrayList<Entity>(Arrays.asList(new Entity[] { null, null, null, null }));
    public FoodType ingredient = null;
}
