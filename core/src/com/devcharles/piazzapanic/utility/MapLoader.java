package com.devcharles.piazzapanic.utility;

import java.beans.VetoableChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.devcharles.piazzapanic.components.LockedComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.StationComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.utility.Station.StationType;
import com.devcharles.piazzapanic.utility.Station.itemDisplayDir;
import com.devcharles.piazzapanic.utility.box2d.Box2dLocation;
import com.devcharles.piazzapanic.utility.box2d.LightBuilder;
import com.devcharles.piazzapanic.utility.box2d.MapBodyBuilder;

import box2dLight.RayHandler;

/**
 * Loads and owns the {@link TiledMap} object. Creates entities from map
 * metadata.
 */
public class MapLoader {

    public TiledMap map;

    private int ppt = 16;

    private EntityFactory factory;

    // Object properties
    static final String lightIdProperty = "lightID";
    static final String cookSpawnPoint = "cookspawnpoint";
    static final String aiSpawnPoint = "aispawnpoint";
    static final String aiObjective = "aiobjective";

    static final String powerupSpawner = "PowerupSpawnPoint";

    // Layers relevant to loading the map
    static final String objectLayer = "MapObjects";
    static final String collisionLayer = "Obstacles";
    static final String stationLayer = "station";
    static final String counterTopLayer = "countertop";

    // Tile properties
    static final String stationIdProperty = "stationID";
    static final String ingredientTypeProperty = "ingredientType";

    // Primarily used for testing purposes
    private Map<Integer, Box2dLocation> aiObjectives;

    public ArrayList<Vector2> cookSpawns;

    // Required for FR_INVESTMENT, handles locking of stations on first load
    boolean lockedCookMade = false;
    boolean lockedCutMade = false;
    boolean lockedGrillMade = false;

    /**
     * Load the {@link TiledMap} from a {@code .tmx} file.
     * 
     * @param path    Path to map file.
     * @param ppt     Pixels per tile (default 16).
     * @param factory {@link EntityFactory} instance to create entities based on the
     *                map metadata.
     */
    public MapLoader(String path, Integer ppt, EntityFactory factory) {
        if (ppt != null) {
            this.ppt = ppt;
        }
        if (path != null) {
            map = new TmxMapLoader().load(path);
        } else {
            map = new TmxMapLoader().load("v2/map.tmx");
        }
        this.cookSpawns = new ArrayList<Vector2>();
        this.factory = factory;
    }

    /**
     * 
     * @param world The Box2D world instance to add bodies to.
     * @return The bodies created from the map.
     */
    public Array<Body> buildCollisions(World world) {
        return MapBodyBuilder.buildShapes(map, ppt, world);
    }

    /**
     * Create lights, spawnpoints, AI paths from map metadata.
     * 
     * @param engine     Ashley {@link Engine} instance.
     * @param rayHandler {@link RayHandler} to add lights to.
     */
    public void buildFromObjects(Engine engine, RayHandler rayHandler) {
        MapObjects objects = map.getLayers().get(objectLayer).getObjects();

        aiObjectives = new HashMap<Integer, Box2dLocation>();

        for (MapObject mapObject : objects) {

            if (mapObject instanceof RectangleMapObject) {
                MapProperties properties = mapObject.getProperties();
                RectangleMapObject point = (RectangleMapObject) mapObject;

                Vector2 pos = new Vector2(point.getRectangle().x / ppt, point.getRectangle().y / ppt);

                if (properties.containsKey(lightIdProperty)) {
                    int lightID = (int) properties.get(lightIdProperty);
                    Gdx.app.log("map parsing",
                            String.format("Light typeid %d at x:%.2f y:%.2f", lightID, pos.x, pos.y));
                    switch (lightID) {
                        case 0:
                            LightBuilder.createPointLight(rayHandler, pos.x, pos.y, Color.TAN.cpy().sub(0, 0, 0, 0.25f),
                                    10, true);
                            break;
                        case 1:
                            LightBuilder.createPointLight(rayHandler, pos.x, pos.y, Color.TAN.cpy().sub(0, 0, 0, 0.5f),
                                    0.8f, false);
                            break;
                        case 2:
                            LightBuilder.createRoomLight(rayHandler, pos.x, pos.y, Color.TAN.cpy().sub(0, 0, 0, 0.25f),
                                    25, true);
                            break;
                        case 3:
                            LightBuilder.createRoomLight(rayHandler, pos.x, pos.y, Color.TAN.cpy().sub(0, 0, 0, 0.25f),
                                    25, false);
                            break;
                    }

                } else if (properties.containsKey(cookSpawnPoint)) {
                    int cookID = (int) properties.get(cookSpawnPoint);
                    Gdx.app.log("map parsing", String.format("Cook spawn point at x:%.2f y:%.2f", pos.x, pos.y));
                    Entity cook = factory.createCook((int) pos.x, (int) pos.y);
                    cookSpawns.add(new Vector2(pos.x, pos.y));
                    if (cookID == 0) {
                        cook.add(new PlayerComponent());
                    }

                } else if (properties.containsKey(aiSpawnPoint)) {
                    Gdx.app.log("map parsing", String.format("Ai spawn point at x:%.2f y:%.2f", pos.x, pos.y));
                    // Modified to add 2 additional spawn points to facilitate FR_CUSTOMER_FLOW
                    // customer groups
                    aiObjectives.put(-2 - (int) properties.get(aiSpawnPoint), new Box2dLocation(pos, 0));
                } else if (properties.containsKey(aiObjective)) {
                    int objective = (int) properties.get(aiObjective);
                    aiObjectives.put(objective, new Box2dLocation(new Vector2(pos.x, pos.y), (float) (1.5f * Math.PI)));
                    Gdx.app.log("map parsing",
                            String.format("Ai objective %d at x:%.2f y:%.2f", objective, pos.x, pos.y));
                } else if (properties.containsKey(powerupSpawner)) { // Powerup spawners from FR_POWERUPS
                    factory.createPowerupSpawner(pos);
                    Gdx.app.log("map parsing",
                            String.format("Powerup spawner at x:%.2f y:%.2f", pos.x, pos.y));
                }
            }
        }
    }

    /**
     * Get the {@link Map} of objectives the AI can travel to.
     * See the map file in the Tiled editor to preview ai objectives.
     * 
     * @return
     */
    public Map<Integer, Box2dLocation> getObjectives() {
        return aiObjectives;
    }

    /**
     * Create station entities from map metadata. Metadata is given to the tile in
     * Edit Tileset, Tile Properties.
     * 
     * @param engine Ashley {@link Engine} instance.
     * @param world  The Box2D world instance to add sensor bodies to.
     */
    public void buildStations(Engine engine, World world) {
        TiledMapTileLayer stations = (TiledMapTileLayer) (map.getLayers().get(stationLayer));
        TiledMapTileLayer stations_f = (TiledMapTileLayer) (map.getLayers().get(stationLayer + "_f"));
        // Additional map layers counted to implement FR_COUNTER
        TiledMapTileLayer counters = (TiledMapTileLayer) (map.getLayers().get(counterTopLayer));
        TiledMapTileLayer counters_f = (TiledMapTileLayer) (map.getLayers().get(counterTopLayer + "_f"));
        int columns = Math.max(stations.getWidth(), counters.getWidth());
        int rows = Math.max(stations.getHeight(), counters.getHeight());

        Cell currentCell;

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                // Multilayer search now handles all four layers instead of the two origianlly
                // done through an inline if statement
                currentCell = stations.getCell(i, j);
                if (currentCell == null) {
                    currentCell = stations_f.getCell(i, j);
                }
                if (currentCell == null) {
                    currentCell = counters.getCell(i, j);
                }
                if (currentCell == null) {
                    currentCell = counters_f.getCell(i, j);
                }
                if (currentCell != null) {
                    Object object = currentCell.getTile().getProperties().get(stationIdProperty);
                    if (object != null && object instanceof Integer) {
                        StationType stationType = StationType.from((int) object);

                        FoodType ingredientType = null;
                        if (stationType == StationType.ingredient) {
                            ingredientType = FoodType
                                    .from((Integer) currentCell.getTile().getProperties().get(ingredientTypeProperty));
                        }
                        System.out.println("Creating station at " + i + ", " + j + " of type " + stationType);
                        Boolean locked = false;
                        switch (stationType) { // Apply locking of stations to implement FR_INVESTMENT
                            case cutting_board:
                                if (lockedCutMade)
                                    break;
                                lockedCutMade = true;
                                locked = true;
                                break;
                            case grill:
                                if (lockedGrillMade)
                                    break;
                                lockedGrillMade = true;
                                locked = true;
                                break;
                            case oven:
                                if (lockedCookMade)
                                    break;
                                lockedCookMade = true;
                                locked = true;
                                break;
                            default:
                                break;
                        }
                        Entity station = factory.createStation(stationType, new Vector2((i * 2) + 1, (j * 2) + 1),
                                ingredientType, locked);
                        if (locked)
                            LockedComponent.lockedStations.add(station);
                        if (stationType == StationType.counter) {
                            itemDisplayDir direction = itemDisplayDir.valueOf(
                                    (String) currentCell.getTile().getProperties().get("inventoryDirection"));
                            station.getComponent(StationComponent.class).direction = direction;
                        }
                    }
                }
            }
        }
    }

    // Mostly used for testing purposes
    public ArrayList<Vector2> GetCookSpawns() {
        return cookSpawns;
    }
}
