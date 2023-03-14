package com.devcharles.piazzapanic.componentsystems;
import org.junit.*;

import com.devcharles.piazzapanic.components.FoodComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.input.KeyboardInput;
import com.devcharles.piazzapanic.utility.EntityFactory;

public class testPlayerControlSystem {
    @Test
    public void testPlayerPickUpItem(){
        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(new KeyboardInput(), null); // Do I need to add a new Pooledengine here?
        PlayerComponent testPlayerComponent = new PlayerComponent();
        EntityFactory testEntityFactory = new EntityFactory(null, null);
        
        FoodComponent testFoodType = new FoodComponent();
        testFoodType.type = FoodType.unformedPatty; // From the FoodType: unformedPatty(1)
        testEntityFactory.createFood(testFoodType.type);
        testPlayerControlSystem.processEntity(null, 0);
    }
}
