package com.devcharles.piazzapanic;

import org.junit.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;

public class testAssets {
    @Test
    /**
     * Tests if the required game assets are present (e.g. textures)
     */
    public void testAssetsPresent() {
        new testEnvironment();
        // Check menu images and tutorial images are present
        Assert.assertTrue(Gdx.files.internal("mainMenuImage.png").exists());
        Assert.assertTrue(Gdx.files.internal("bucket.png").exists());
        Assert.assertTrue(Gdx.files.internal("droplet.png").exists());
        Assert.assertTrue(Gdx.files.internal("recipe0.png").exists());
        Assert.assertTrue(Gdx.files.internal("recipe1.png").exists());
        //TODO - add the further recipes
        Assert.assertTrue(Gdx.files.internal("tutorial0.png").exists());
        Assert.assertTrue(Gdx.files.internal("tutorial1.png").exists());
        Assert.assertTrue(Gdx.files.internal("tutorial2.png").exists());
        Assert.assertTrue(Gdx.files.internal("tutorial3.png").exists());
        Assert.assertTrue(Gdx.files.internal("tutorial4.png").exists());
        Assert.assertTrue(Gdx.files.internal("tutorial5.png").exists());
        Assert.assertTrue(Gdx.files.internal("tutorial6.png").exists());
        Assert.assertTrue(Gdx.files.internal("tutorial7.png").exists());
        Assert.assertTrue(Gdx.files.internal("tutorial8.png").exists());
        Assert.assertTrue(Gdx.files.internal("tutorial9.png").exists());
        Assert.assertTrue(Gdx.files.internal("tutorial10.png").exists());

        // Check that Boosts are present
        Assert.assertTrue(Gdx.files.internal("boosts/boostCook.png").exists());
        Assert.assertTrue(Gdx.files.internal("boosts/boostCut.png").exists());
        Assert.assertTrue(Gdx.files.internal("boosts/boostError.png").exists());
        Assert.assertTrue(Gdx.files.internal("boosts/boostOrder.png").exists());
        Assert.assertTrue(Gdx.files.internal("boosts/boostSpeed.png").exists());
        Assert.assertTrue(Gdx.files.internal("boosts/boostTimeFreeze.png").exists());

        // Check raw Craftacular theme assets
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/armor-bg.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/armor.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/button-disabled.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/button-hover.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/button.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/cell.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/craftacular-mockup.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/cursor.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/dirt.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/font-bold-export.fnt").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/font-bold-export.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/font-export.fnt").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/font-export.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/font-title-export.fnt").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/font-title-export.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/heart-bg.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/heart.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/meat-bg.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/meat.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/scroll-bg.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/scroll-horizontal.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/scroll-knob-horizontal.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/scroll-knob-vertical.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/scroll-vertical.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/slider-knob.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/slider.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/text-field.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/tooltip.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/underline.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/white.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/window.9.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/xp-bg.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/xp.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/xp.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/raw/xp.png").exists());

        // Check remaining Craftacular assets
        Assert.assertTrue(Gdx.files.internal("craftacular/preview.gif").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/preview.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/skin/craftacular-ui.atlas").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/skin/craftacular-ui.json").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/skin/craftacular-ui.png").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/skin/font-bold-export.fnt").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/skin/font-export.fnt").exists());
        Assert.assertTrue(Gdx.files.internal("craftacular/skin/font-title-export.fnt").exists());

        // Check specific game assets.
        Assert.assertTrue(Gdx.files.internal("v2/background.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/background_padded.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/food.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/GameScreen.jpg").exists());
        Assert.assertTrue(Gdx.files.internal("v2/map.tmx").exists());
        Assert.assertTrue(Gdx.files.internal("v2/maps.tiled-project").exists());
        Assert.assertTrue(Gdx.files.internal("v2/maps.tiled-session").exists());
        Assert.assertTrue(Gdx.files.internal("v2/objects.tsx").exists());
        Assert.assertTrue(Gdx.files.internal("v2/READ ME.txt").exists());
        Assert.assertTrue(Gdx.files.internal("v2/stations.tsx").exists());
        Assert.assertTrue(Gdx.files.internal("v2/stations_chef.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/stations_chef_padded.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/tileset_32.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/tileset_32.tsx").exists());

        // Check chef assets are present
        Assert.assertTrue(Gdx.files.internal("v2/chef/1.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/chef/1_crate.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/chef/1_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/chef/2.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/chef/2_crate.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/chef/2_holding.png").exists());

        // Check customer assets are present
        Assert.assertTrue(Gdx.files.internal("v2/customer/1.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/10.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/10_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/11.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/11_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/12.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/12_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/13.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/13_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/14.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/14_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/15.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/15_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/1_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/2.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/2_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/3.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/3_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/4.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/4_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/5.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/5_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/6.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/6_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/7.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/7_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/8.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/8_holding.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/9.png").exists());
        Assert.assertTrue(Gdx.files.internal("v2/customer/9_holding.png").exists());
    }
}
