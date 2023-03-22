package com.devcharles.piazzapanic;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;

public class testEnvironment extends HeadlessApplication {

    public testEnvironment() {
        super(new ApplicationListener() {

            @Override
            public void create() {

            }

            @Override
            public void resize(int width, int height) {

            }

            @Override
            public void render() {

            }

            @Override
            public void pause() {

            }

            @Override
            public void resume() {

            }

            @Override
            public void dispose() {

            }

        });
        Gdx.gl = new emptyGL20();
    }

}
