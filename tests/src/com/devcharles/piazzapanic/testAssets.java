package com.devcharles.piazzapanic;
import org.junit.*;
import com.badlogic.gdx.Gdx;

public class testAssets {  
    @Test
    public void testAssetsPresent(){
        // TODO - this returns a NullPointerException and i DONT KNOW WHY
        Gdx.files.internal("mainMenuImage.png");
    }
    
}
