package com.devcharles.piazzapanic.components;

import static org.junit.Assert.assertTrue;

import org.junit.*;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.componentsystems.StationSystem;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.Station.StationType;

public class testOverCookingComponent {
    @Test
    /**
     * Check foods become spoiled if processing too long.
     */
    public void testOverCooking() {
        // Initialise environment
        testEnvironment environment = new testEnvironment();
        PooledEngine engine = new PooledEngine();
        StationSystem stationSystem = new StationSystem(null, environment.factory);
        EntityFactory entityFactory = environment.factory;
        engine.addSystem(stationSystem);

        // Create 2 foods that each require a different type of cooking.
        Entity testPattySuccess = entityFactory.createFood(FoodType.formedPatty); // Grill.
        Entity testPattySpoil = entityFactory.createFood(FoodType.formedPatty);

        Entity testPotatoSuccess = entityFactory.createFood(FoodType.potato); // Oven.
        Entity testPotatoSpoil = entityFactory.createFood(FoodType.potato);

        // Create a chef to put things on the station.
        Entity testCook = entityFactory.createCook(0, 0);
        ControllableComponent chefComponent = Mappers.controllable.get(testCook);
        chefComponent.currentFood.pushItem(testPattySuccess, testCook);
        chefComponent.currentFood.pushItem(testPattySpoil, testCook);
        chefComponent.currentFood.pushItem(testPotatoSuccess, testCook);
        chefComponent.currentFood.pushItem(testPotatoSpoil, testCook);

        // Create stations to process food.
        Entity testOvenSuccess = entityFactory.createStation(StationType.oven, new Vector2(1, 0), null, false);
        Entity testOvenSpoil = entityFactory.createStation(StationType.oven, new Vector2(2, 0), null, false);
        Entity testGrillSuccess = entityFactory.createStation(StationType.grill, new Vector2(3, 0), null, false);
        Entity testGrillSpoil = entityFactory.createStation(StationType.grill, new Vector2(4, 0), null, false);

        StationComponent ovenSuccessComponent = Mappers.station.get(testOvenSuccess);
        StationComponent ovenSpoilComponent = Mappers.station.get(testOvenSpoil);
        StationComponent grillSuccessComponent = Mappers.station.get(testGrillSuccess);
        StationComponent grillSpoilComponent = Mappers.station.get(testGrillSpoil);

        // Force set the interacting cook as our test cook
        ovenSpoilComponent.interactingCook = testCook;
        grillSpoilComponent.interactingCook = testCook;
        ovenSuccessComponent.interactingCook = testCook;
        grillSuccessComponent.interactingCook = testCook;

        // This is used lots of times below, it gets overwritten each time so no worry
        // about leaked references.
        CookingComponent foodCookingComponent;

        // Run the order of putting food on and using stations.
        stationSystem.processStation(chefComponent, ovenSuccessComponent);
        stationSystem.stationTick(ovenSuccessComponent, 5.1f);
        stationSystem.interactStation(ovenSuccessComponent);
        // Quick sanity check to make sure the food is ready to start the second stage
        // of processing.
        foodCookingComponent = Mappers.cooking.get(ovenSuccessComponent.food.get(0));
        Assert.assertTrue(foodCookingComponent.processed);
        Assert.assertTrue(foodCookingComponent.timer.peakElapsed() == 0);
        // Finish cooking the food.
        stationSystem.stationTick(ovenSuccessComponent, 5.1f); // Tick twice to give the OverCookingComponent a chance
                                                               // to check whether the food spoils.
        stationSystem.stationTick(ovenSuccessComponent, 0.1f);
        // TODO: Now pick up the food again. Is it spoiled?

    }
}
