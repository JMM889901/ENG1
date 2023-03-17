package com.devcharles.piazzapanic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.*;

public class testAssets {  
    @Test
    public void testAssetsPresent(){
        Path path = Paths.get("does-not-exist.txt");
        Assert.assertFalse(Files.exists(path));
    }
    
}
