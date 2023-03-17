package com.devcharles.piazzapanic;
import org.junit.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;

public class testAssets {  
    @Test
    public void testAssetsPresent(){
        // TODO - this returns a NullPointerException and i DONT KNOW WHY
        new HeadlessApplication(new PiazzaPanic());
        Assert.assertTrue(Gdx.files.internal("/assets/recipe0.png").exists());
    }
    
}
