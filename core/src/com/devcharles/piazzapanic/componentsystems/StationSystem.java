package com.devcharles.piazzapanic.componentsystems;

import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.FoodComponent;
import com.devcharles.piazzapanic.components.LockedComponent;
import com.devcharles.piazzapanic.components.OvercookingComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.StationComponent;
import com.devcharles.piazzapanic.components.TintComponent;
import com.devcharles.piazzapanic.components.CookingComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.components.Powerups.cookBoostComponent;
import com.devcharles.piazzapanic.input.KeyboardInput;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.GdxTimer;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.Station;
import com.devcharles.piazzapanic.utility.Station.StationType;

/**
 * This system manages player-station interaction and station food processing.
 */
public class StationSystem extends IteratingSystem {

    KeyboardInput input;

    boolean interactingStation = false;

    EntityFactory factory;

    // This is the colour that an ingredient flashes with when it needs to be
    // flipped/turned over/etc.
    private TintComponent readyTint;
    private TintComponent overcookedTint;
    private float tickAccumulator = 0;

    public StationSystem(KeyboardInput input, EntityFactory factory) {
        super(Family.all(StationComponent.class).get());
        this.input = input;
        this.factory = factory;
    }

    @Override
    public void update(float deltaTime) {
        tickAccumulator += deltaTime;
        super.update(deltaTime);
        if (tickAccumulator > 0.5f) {
            tickAccumulator -= 0.5f;
        }
    }

    /**
     * Process a station in a tick, i.e. picking up and putting down to a station.
     * <p>
     * This interacts with the currently playing/active cook by grabbing it
     * from the various engines/systems running.
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StationComponent station = Mappers.station.get(entity);

        if (entity.getComponent(LockedComponent.class) != null) {
            return;// Station is not unlocked yet
        }

        stationTick(station, deltaTime);

        if (station.interactingCook != null) {

            PlayerComponent player = Mappers.player.get(station.interactingCook);

            if (player == null) {
                return;
            }

            if (player.putDown) {
                player.putDown = false;

                ControllableComponent controllable = Mappers.controllable.get(station.interactingCook);

                switch (station.type) {
                    case ingredient:
                        // Using the put down key on an ingredient station will also pick up food.
                        controllable.currentFood.pushItem(factory.createFood(station.ingredient),
                                station.interactingCook);
                        break;
                    case bin:
                        processBin(controllable);
                        break;

                    case serve:
                        processServe(station.interactingCook);
                        break;

                    case counterBack:
                    case counter:
                        processCounterTop(controllable, station);
                        break;
                    default:
                        processStation(controllable, station);
                        break;
                }
            } else if (player.pickUp) {
                player.pickUp = false;

                ControllableComponent controllable = Mappers.controllable.get(station.interactingCook);

                switch (station.type) {
                    // Ingredient station: create the food first, then give it to the player.
                    case ingredient:
                        controllable.currentFood.pushItem(factory.createFood(station.ingredient),
                                station.interactingCook);
                        break;

                    // Bin: do nothing (can't take out of bin).
                    case bin:
                        break;

                    // Serve: do nothing (can't take out of serving station).
                    case serve:
                        break;

                    // Default (counter, chopping board etc): take the food from the station if it
                    // allows.
                    default:
                        stationPickup(station, controllable);
                        break;
                }
            } else if (player.interact) {
                // Interacting with a station is basically setting a flag that a station has
                // been
                // interacted with.
                player.interact = false;
                interactStation(station);

            } else if (player.compileMeal) {
                player.compileMeal = false;

                // If the player tries to compile a meal while not at a serving station, it
                // doesn't matter, the code doesn't need to handle that.
                if (station.type == StationType.serve) {
                    processServe(station.interactingCook);
                }
            }
        }
    }

    private void processCounterTop(ControllableComponent controllable, StationComponent station) {

        if (controllable.currentFood.isEmpty()) {
            return;
        }

        Gdx.app.log("putDown on countertop", Mappers.food.get(controllable.currentFood.peek()).type.name());

        FoodComponent food = Mappers.food.get(controllable.currentFood.peek());

        int foodIndex = station.food.indexOf(null);

        // If there is space on the station
        if (foodIndex != -1) {
            // Pop if off player stack
            // Store in station
            station.food.set(foodIndex, controllable.currentFood.pop());
        } else {
            return;
        }

    }

    /**
     * Try and process the food from the player.
     */
    private void processStation(ControllableComponent controllable, StationComponent station) {

        if (controllable.currentFood.isEmpty()) {
            return;
        }

        Gdx.app.log("putDown", Mappers.food.get(controllable.currentFood.peek()).type.name());

        FoodComponent food = Mappers.food.get(controllable.currentFood.peek());

        HashMap<FoodType, FoodType> recipes = Station.recipeMap.get(station.type);

        if (recipes == null) {
            return;
        }

        FoodType result = recipes.get(food.type);

        if (result == null) {
            return;
        }

        int foodIndex = station.food.indexOf(null);

        // If there is space on the station
        if (foodIndex != -1) {
            // Pop if off player stack
            // Store in station
            station.food.set(foodIndex, controllable.currentFood.pop());
        } else {
            return;
        }

        // success

        // Cooking timer is set here by default when initialising CookingComponent.
        CookingComponent cooking = getEngine().createComponent(CookingComponent.class);

        // If the cook is currently boosted, we overwrite the default timer.
        if (station.interactingCook.getComponent(cookBoostComponent.class) != null) {
            cooking.timer = new GdxTimer(cookBoostComponent.boostTime, false, false);
        }
        cooking.timer.start();

        station.food.get(foodIndex).add(cooking);

        Gdx.app.log("Food processed", String.format("%s turned into %s", food.type, result));

    }

    /**
     * Perform special action (flipping patties, etc.)
     * 
     * @param station the station the action is being performed on.
     */
    private void interactStation(StationComponent station) {
        for (Entity food : station.food) {
            if (food == null || !Mappers.cooking.has(food)) {
                continue;
            }

            CookingComponent cooking = Mappers.cooking.get(food);

            // Check if it's ready without ticking the timer
            boolean ready = cooking.timer.tick(0);

            if (ready && !cooking.processed) {
                food.remove(TintComponent.class);
                cooking.processed = true;
                cooking.timer.reset();
                return;
            }
        }
    }

    /**
     * Try to combine the ingredients at the top of the player's inventory stack
     * (max 3) into a ready meal.
     * 
     * @param cook the cook whos inventory is being used for creating the food.
     */
    private void processServe(Entity cook) {
        ControllableComponent controllable = Mappers.controllable.get(cook);

        // If there is only one (or zero) ingredient[s] then skip testing for recipes.
        // (This is a premature optimisation.)
        if (controllable.currentFood.size() < 2) {
            return;
        }

        // Assume that the player is trying to make a meal with the first 2 ingredients,
        // so if you
        // have 3 ingredients but the first 2 make a baked potato, ignore the third.
        int count = 2;
        FoodType result = tryServe(controllable, count);

        if (result == null) {
            result = tryServe(controllable, ++count);
            if (result == null) {
                return;
            }
        }

        for (int i = 0; i < count; i++) {
            Entity e = controllable.currentFood.pop();
            getEngine().removeEntity(e);
        }

        controllable.currentFood.pushItem(factory.createFood(result), cook);

    }

    /**
     * Attempt to create a food.
     * Allows combining ingredients for non servable as well such as raw pizza
     * 
     * @param count number of ingredients to combine
     */
    private FoodType tryServe(ControllableComponent controllable, int count) {
        Set<FoodType> ingredients = new HashSet<FoodType>();
        int i = 0;
        for (Entity foodEntity : controllable.currentFood) {
            if (i > count - 1) {
                break;
            }
            if (foodEntity.getComponent(OvercookingComponent.class) != null
                    && foodEntity.getComponent(OvercookingComponent.class).processed) {
                System.out.println("Overcooked food");
                continue;
            }
            ingredients.add(Mappers.food.get(foodEntity).type);

            i++;
        }
        FoodType recipe;
        recipe = Station.serveRecipes.get(ingredients);
        if (recipe == null) {
            recipe = Station.combineRecipes.get(ingredients);
        }

        return recipe;
    }

    /**
     * Destroy the top food in the inventory of a cook.
     */
    private void processBin(ControllableComponent controllable) {
        if (controllable.currentFood.isEmpty()) {
            return;
        }

        Entity e = controllable.currentFood.pop();
        getEngine().removeEntity(e);
    }

    /**
     * Pick up ready food from a station
     */
    private void stationPickup(StationComponent station, ControllableComponent controllable) {
        // For each food item stored in a station:...
        for (Entity foodEntity : station.food) {

            // Check first the food is actual food (ie not null) and that it is ready (ie no
            // longer
            // cooking, so no longer has the cooking component).
            if (foodEntity != null && !Mappers.cooking.has(foodEntity)) {

                // "Push" the food into the player's inventory, if this succeeds (ie the
                // player's
                // inventory is not full), then remove the food from the station.
                if (controllable.currentFood.pushItem(foodEntity, station.interactingCook)) {
                    station.food.set(station.food.indexOf(foodEntity), null);
                    Mappers.transform.get(foodEntity).scale.set(1, 1);
                    Gdx.app.log("Picked up", Mappers.food.get(foodEntity).type.toString());
                }
                return;
            }
        }
    }

    /**
     * Cook the food in the station. This progresses the timer in the food being
     * cooked in the station.
     * 
     * @param station
     * @param deltaTime
     */
    private void stationTick(StationComponent station, float deltaTime) {
        if (station.type == StationType.cutting_board && station.interactingCook == null) {
            return;
        }

        if (station.type == StationType.counter || station.type == StationType.counterBack) {
            return;
        }

        for (Entity foodEntity : station.food) {

            if (foodEntity == null) {
                continue;
            }

            if (Mappers.cooking.has(foodEntity)) {
                CookingComponent cooking = Mappers.cooking.get(foodEntity);

                boolean ready = cooking.timer.tick(deltaTime);

                cooking.debugPrintableTimer += deltaTime;

                if (ready && cooking.processed) {
                    cooking.timer.stop();
                    cooking.timer.reset();
                    cooking.debugPrintableTimer = 0;
                    FoodComponent food = Mappers.food.get(foodEntity);
                    // Process the food into it's next form
                    food.type = Station.recipeMap.get(station.type).get(food.type);
                    Mappers.texture.get(foodEntity).region = EntityFactory.getFoodTexture(food.type);
                    foodEntity.remove(CookingComponent.class);
                    Gdx.app.log("Food ready", food.type.name());
                    OvercookingComponent overcooking = getEngine().createComponent(OvercookingComponent.class);
                    overcooking.timer.start();
                    foodEntity.add(overcooking);
                } else if (ready) { // Handles flashing food every 0.5 seconds

                    if (tickAccumulator > 0.5f) {

                        if (!Mappers.tint.has(foodEntity)) {
                            foodEntity.add(readyTint);
                        } else {
                            foodEntity.remove(TintComponent.class);
                        }
                    }

                }
                if ((tickAccumulator > 0.5f && cooking.debugPrintableTimer > 0.5f) && !cooking.processed)
                    System.out.println(cooking.debugPrintableTimer);// TEMP
            } else if (station.type != StationType.cutting_board && Mappers.overcooking.has(foodEntity)) {
                OvercookingComponent overcooking = Mappers.overcooking.get(foodEntity);
                // Handle overcooking food
                boolean ready = overcooking.timer.tick(deltaTime);

                if (ready) {
                    overcooking.timer.stop();
                    overcooking.timer.reset();
                    FoodComponent food = Mappers.food.get(foodEntity);
                    // Process the food into it's next form
                    // food.type = Station.recipeMap.get(station.type).get(food.type);
                    // TODO: This stuff
                    // Mappers.texture.get(foodEntity).region =
                    // EntityFactory.getFoodTexture(food.type);
                    overcooking.processed = true;
                    foodEntity.add(overcookedTint);
                    Gdx.app.log("Food overdone", food.type.name());
                }
            }

        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        readyTint = getEngine().createComponent(TintComponent.class);
        readyTint.tint = Color.ORANGE;
        overcookedTint = getEngine().createComponent(TintComponent.class);
        overcookedTint.tint = Color.GREEN;

    }

}
