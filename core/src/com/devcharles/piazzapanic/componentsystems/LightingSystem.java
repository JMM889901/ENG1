package com.devcharles.piazzapanic.componentsystems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import box2dLight.RayHandler;

/**
 * Most lighting stuff is done for us but we need to give it information, that's what we do here.
 */
public class LightingSystem extends EntitySystem {

    private RayHandler rayHandler;
    private OrthographicCamera camera;

    public LightingSystem(RayHandler rayHandler, OrthographicCamera camera) {
        this.rayHandler = rayHandler;
        this.camera = camera;
    }

    /**
     * This only serves to tell the lighting system where the camera is. RayHandler does the rest.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
    }
 }
