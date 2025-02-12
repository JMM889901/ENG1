package com.devcharles.piazzapanic.componentsystems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.undo.StateEdit;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.components.AIAgentComponent;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.CustomerComponent;
import com.devcharles.piazzapanic.components.ItemComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.scene2d.Hud;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.GdxTimer;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.box2d.Box2dLocation;
import com.devcharles.piazzapanic.utility.box2d.Box2dRadiusProximity;

/**
 * Controls the AI Customers, creates orders.
 */
public class CustomerAISystem extends IteratingSystem {

    private final Map<Integer, Box2dLocation> objectives;
    private final Map<Integer, Boolean> objectiveTaken;

    private final World world;
    private final EntityFactory factory;
    public int numOfCustomerTotal = 0;
    public int numActiveCustomers = 0;
    private final Hud hud;
    private final Integer[] reputationPoints;
    private static int maxCustomers = (int) Double.POSITIVE_INFINITY;
    private static int difficulty; // This is just a record to be recalled when saving, this doesn't set anything.
    private static int maxActiveCustomers = 7;
    public boolean firstSpawn = true;
    public static int MaxGroupSize = 3;// easy is 1(this is kind of a hack ig), hard is 3 Spawning more than 3 may
                                       // cause funky stuff so dont
    private int spawnedThisGroup = 0;
    public static int SpawnTime = 30000;// easy is 30000, hard is 10000
    public static int SpawnRampTime = 1;// Seconds for group spawn frequency to ramp up, should be 300
    private final GdxTimer spawnTimer = new GdxTimer(SpawnTime, false, true);

    // https://stackoverflow.com/questions/33997169/when-to-use-anonymous-classes
    // https://www.baeldung.com/java-anonymous-classes
    // Anonymous classes are weird, I've added a whole bunch more comments,
    // hopefully it's more clear now. - Joss
    // `customers` is an anonymous class, based of ArrayList<Entity> but it does
    // fancy stuff when
    // .remove() is called.

    // List of customers, on removal we move the other customers up a place
    // (queueing).
    public final ArrayList<Entity> customers = new ArrayList<Entity>() {
        // Just list of customers apart from it does extra things when you remove a
        // customer off the list. - Joss
        @Override
        public boolean remove(Object o) {
            // For every customer other than "this" one, get it's AI agent controller and
            // tell it
            // to shuffle up the queue.
            for (Entity entity : customers) {
                if (entity != o) {
                    // Get the AIAgentComponent of each customer in the queue (apart from the one
                    // getting removed).
                    AIAgentComponent aiAgent = Mappers.aiAgent.get(entity);

                    // If the customer is not at the front of the queue...
                    if (aiAgent.currentObjective - 1 >= 0) {

                        // ... and that space in the queue is not taken (objectiveTaken is a map of
                        // queue indeces to booleans noting whether there is something in that queue
                        // index)
                        System.out.println("Objective taken: " + objectiveTaken.get(aiAgent.currentObjective - 1));
                        if (!objectiveTaken.containsKey(aiAgent.currentObjective - 1)) {
                            System.out.println("Objective does not exist");
                            continue;// This is a hack, I don't know why it's happening but it's happening
                        }
                        if (!objectiveTaken.get(aiAgent.currentObjective - 1)) {
                            // ... then move the customer up the queue.
                            makeItGoThere(aiAgent, aiAgent.currentObjective - 1);
                        }
                    }

                }
            }
            return super.remove(o);
        }
    };

    public static void setDifficulty(int i) {
        difficulty = i; // Just remember this for saving.

        if (i == 0) {
            MaxGroupSize = 1;
            SpawnTime = 30000;
            SpawnRampTime = 300;
        } else if (i == 1) {
            MaxGroupSize = 2;
            SpawnTime = 20000;
            SpawnRampTime = 200;
        } else if (i == 2) {
            MaxGroupSize = 3;
            SpawnTime = 10000;
            SpawnRampTime = 100;
        }
    }

    public static int getDifficulty() {
        return difficulty;
    }

    public void forceTick(Entity entity, Float deltaTime) {
        tickCustomerTimer(entity, entity.getComponent(CustomerComponent.class), deltaTime);
    }

    /**
     * Instantiate the system.
     * 
     * @param objectives       Map of objectives available
     * @param world            Box2D {@link World} for AI and disposing of customer
     *                         entities.
     * @param factory          {@link EntityFactory} for creating new customers
     * @param hud              Hud for updating orders, reputation
     * @param reputationPoints array-wrapped integer reputation passed by-reference
     * 
     */
    public CustomerAISystem(Map<Integer, Box2dLocation> objectives, World world, EntityFactory factory, Hud hud,
            Integer[] reputationPoints) {
        super(Family.all(AIAgentComponent.class, CustomerComponent.class).get());

        this.hud = hud;
        this.objectives = objectives;
        this.objectiveTaken = new HashMap<Integer, Boolean>();
        this.reputationPoints = reputationPoints;

        // Use a reference to the world to destroy box2d bodies when despawning
        // customers
        this.world = world;
        this.factory = factory;

        spawnTimer.start();
    }

    @Override
    public void update(float deltaTime) {
        if (firstSpawn || (spawnTimer.tick(deltaTime))) {
            firstSpawn = false;
            SpawnCustomerGroup();
        }

        FoodType[] orders = new FoodType[customers.size()];
        int i = 0;
        for (Entity customer : customers) {
            orders[i] = Mappers.customer.get(customer).order;
            i++;
        }

        if (!hud.won && customers.size() == 0 && numOfCustomerTotal == maxCustomers) {
            hud.triggerWin = true;
        }

        super.update(deltaTime);

        hud.updateOrders(orders);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AIAgentComponent aiAgent = Mappers.aiAgent.get(entity);
        CustomerComponent customer = Mappers.customer.get(entity);
        TransformComponent transform = Mappers.transform.get(entity);

        // Once the customer has got their food (setting customer.food to a non-null
        // value), they
        // wander off to the right. This code destroys the customer once they have gone
        // far enough.
        if (customer.food != null && transform.position.x >= (objectives.get(-1).getPosition().x - 2)) {
            destroyCustomer(entity);
            return;
        }

        // Set the customer's location ID to be that of their position in the queue
        // (customers.size() is the number of customers.)
        if (aiAgent.steeringBody.getSteeringBehavior() == null) {
            makeItGoThere(aiAgent, customers.size() - 1);
        }

        aiAgent.steeringBody.update(deltaTime);

        // lower reputation points if they have waited longer than time alloted (1 min)
        tickCustomerTimer(entity, customer, deltaTime);

        if (customer.interactingCook != null) {
            PlayerComponent player = Mappers.player.get(customer.interactingCook);

            // If there is both a player, and they are pressing the giveToCustomer key, then
            // continue below...
            if (player == null || !player.giveToCustomer) {
                return;
            }
            player.giveToCustomer = false;

            ControllableComponent cook = Mappers.controllable.get(customer.interactingCook);

            if (cook.currentFood.isEmpty()) {
                return;
            }

            Entity food = cook.currentFood.getLast();

            if (Mappers.food.get(food).type == customer.order) {
                // Fulfill order
                Gdx.app.log("Order success", customer.order.name());
                fulfillOrder(entity, customer, cook.currentFood.pop());

            }

        }
    }

    void SpawnCustomerGroup() {
        if (numActiveCustomers < maxActiveCustomers
                && numOfCustomerTotal < maxCustomers) {
            if (ShouldSpawnGroup()) {
                int numToSpawn = ThreadLocalRandom.current().nextInt(1,
                        Math.min(Math.min(maxActiveCustomers - numActiveCustomers, maxCustomers - numOfCustomerTotal),
                                MaxGroupSize) + 1);// Not a huge fan of chaining min but what can you do
                for (int i = 0; i < numToSpawn; i++) {
                    SpawnCustomer();
                    spawnedThisGroup++;
                }
                spawnedThisGroup = 0;
                return;
            }
            SpawnCustomer();
        }
    }

    boolean ShouldSpawnGroup() {
        // Random chance scaling with difficulty and time
        if (maxActiveCustomers - numActiveCustomers < 2 || maxCustomers - numOfCustomerTotal < 2)
            return false;
        Float chance = (float) (hud.customerTimer / SpawnRampTime); // yes this scales over 1, lol, lmao even
        if (chance > 0.8) {
            chance = 0.8f;
        }
        return ThreadLocalRandom.current().nextFloat() < chance;
    }

    void SpawnCustomer() {
        Entity newCustomer = factory.createCustomer(objectives.get(-2 - spawnedThisGroup).getPosition());
        customers.add(newCustomer);
        numOfCustomerTotal++;
        numActiveCustomers++;
        Mappers.customer.get(newCustomer).timer.start();
        makeItGoThere(newCustomer.getComponent(AIAgentComponent.class), customers.size() - 1);
    }

    void tickCustomerTimer(Entity entity, CustomerComponent customer, float deltaTime) {
        if (customer.timer.tick(deltaTime)) {
            if (reputationPoints[0] > 0) {
                reputationPoints[0]--;
            }
            customer.timer.stop();
        }
    }

    /**
     * Remove the customer from the {@link World} and remove their entity.
     */
    private void destroyCustomer(Entity customer) {
        getEngine().removeEntity(Mappers.customer.get(customer).food);
        world.destroyBody(Mappers.b2body.get(customer).body);
        getEngine().removeEntity(customer);
    }

    /**
     * Give the customer an objetive to go to.
     * 
     * @param locationID and id from {@link CustomerAISystem.objectives}
     */
    public void makeItGoThere(AIAgentComponent aiAgent, int locationID) {
        objectiveTaken.put(aiAgent.currentObjective, false);

        if (objectives == null)
            return;// Hack for testing
        Box2dLocation there = objectives.get(locationID);

        Arrive<Vector2> arrive = new Arrive<Vector2>(aiAgent.steeringBody)
                .setTimeToTarget(0.1f)
                .setArrivalTolerance(0.25f)
                .setDecelerationRadius(2)
                .setTarget(there);

        Proximity<Vector2> proximity = new Box2dRadiusProximity(aiAgent.steeringBody, world, 1f);
        CollisionAvoidance<Vector2> collisionAvoidance = new CollisionAvoidance<Vector2>(
                aiAgent.steeringBody, proximity);

        PrioritySteering<Vector2> prioritySteering = new PrioritySteering<Vector2>(aiAgent.steeringBody)
                .add(collisionAvoidance)
                .add(arrive);

        aiAgent.steeringBody.setSteeringBehavior(prioritySteering);
        aiAgent.currentObjective = locationID;
        objectiveTaken.put(aiAgent.currentObjective, true);

        if (locationID == -1) {
            aiAgent.steeringBody.setOrientation(0);
        } else {
            aiAgent.steeringBody.setOrientation((float) (1.5f * Math.PI));
        }
    }

    /**
     * Give customer food, send them away and remove the order from the list
     * 
     * @param entity   The actual customer that walks about.
     * @param customer The component properties of the customer.
     */
    private void fulfillOrder(Entity entity, CustomerComponent customer, Entity foodEntity) {

        Engine engine = getEngine();

        customer.order = null;

        ItemComponent heldItem = engine.createComponent(ItemComponent.class);
        heldItem.holderTransform = Mappers.transform.get(entity);

        foodEntity.add(heldItem);

        customer.food = foodEntity;

        AIAgentComponent aiAgent = Mappers.aiAgent.get(entity);
        makeItGoThere(aiAgent, -1);

        hud.addMoney(5);
        // Hud.money[0] += 5;

        numActiveCustomers--;

        customer.timer.stop();
        customer.timer.reset();

        customers.remove(entity);
    }

    /**
     * Fulfill the order as above, but determine the food type from the customer's
     * order.
     * 
     * @param entity The actual customer that walks about.
     */
    public void autoFulfillOrder(Entity entity) {
        CustomerComponent customer = entity.getComponent(CustomerComponent.class);
        Gdx.app.log("Order automatically resolved", customer.order.name());
        // This is created automatically rather than taking a food entity from the
        // parameters of
        // the method.
        Entity foodEntity = factory.createFood(customer.order);

        Engine engine = getEngine();

        customer.order = null;

        ItemComponent heldItem = engine.createComponent(ItemComponent.class);
        heldItem.holderTransform = Mappers.transform.get(entity);

        foodEntity.add(heldItem);

        customer.food = foodEntity;

        AIAgentComponent aiAgent = Mappers.aiAgent.get(entity);
        makeItGoThere(aiAgent, -1);

        hud.addMoney(5);
        // Hud.money[0] += 5;

        numActiveCustomers--;

        customer.timer.stop();
        customer.timer.reset();

        customers.remove(entity);
    }

    public static void setMaxCustomers(int Customers) {
        System.out.println("customer max changed from " + CustomerAISystem.maxCustomers + "to " + Customers);
        // since we use instanced systems this should be static
        // but also: Thats alot of effort and we dont have time
        maxCustomers = Customers;
    }

    public static int getMaxCustomers() {
        return maxCustomers;
    }

}
