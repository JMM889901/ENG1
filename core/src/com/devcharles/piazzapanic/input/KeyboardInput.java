package com.devcharles.piazzapanic.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

public class KeyboardInput implements InputProcessor {

    public boolean left, right, up, down;

    public boolean changeCooks;

    public boolean changeCooksReverse;

    public boolean putDown;

    public boolean pickUp;

    public boolean interact;

    public boolean disableHud;

    private static int alternative_keys_progress = 0;
    private static boolean alt_keys = false;
    
    public void clearInputs() {
        left = false;
        right = false;
        up = false;
        down = false;
        changeCooks = false;
        putDown = false;
        pickUp = false;
        interact = false;
        disableHud = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean processed = true;
        switch (keycode) {
            case Keys.LEFT:
            case Keys.A:
                left = true;
                break;
            case Keys.RIGHT:
            case Keys.D:
                right = true;
                break;
            case Keys.UP:
            case Keys.W:
                up = true;
                break;
            case Keys.DOWN:
            case Keys.S:
                down = true;
                break;
            case Keys.F:
                // This is simply because Joss' muscle memory for using F to pick things up is too strong.
                if(alt_keys) {
                    pickUp = true;
                } else {
                    putDown = true;
                }
                
                break;
            case Keys.E:
                if(!alt_keys) {
                    changeCooks = true;
                }
                break;
            case Keys.K:
                changeCooks = true;
                break;
            case Keys.L:
                changeCooksReverse = true;
                break;
            case Keys.R:
                if(alt_keys) {
                    pickUp = true;
                }
                break;
            case Keys.Q:
                interact = true;
                break;
            case Keys.SPACE:
                interact = true;
                if(alt_keys) {
                    putDown = true;
                } else {
                    pickUp = true;
                }
                break;
            case Keys.SHIFT_RIGHT:
                alternative_keys_progress |= 1;
                break;
            case Keys.SHIFT_LEFT:
                alternative_keys_progress |= 2;
                break;
            case Keys.H:
                disableHud = true;
            default:
                processed = false;
        }
        if((alternative_keys_progress & 3) == 3)
        {
            alt_keys = true;
            System.out.println("### Using alternative keys ###");
        }

        return processed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean processed = true;
        switch (keycode) {
            case Keys.LEFT:
            case Keys.A:
                left = false;
                break;
            case Keys.RIGHT:
            case Keys.D:
                right = false;
                break;
            case Keys.UP:
            case Keys.W:
                up = false;
                break;
            case Keys.DOWN:
            case Keys.S:
                down = false;
                break;
            case Keys.E:
                if(!alt_keys) {
                    changeCooks = false;
                }
                break;
            case Keys.K:
                changeCooks = false;
                break;
            case Keys.L:
                changeCooksReverse = false;
                break;
            case Keys.F:
                if((alternative_keys_progress & 4) == 4) {
                    pickUp = false;
                } else {
                    putDown = false;
                }
                break;
            case Keys.R:
                if((alternative_keys_progress & 4) != 4) {
                    pickUp = false;
                }
                break;
            case Keys.Q:
                interact = false;
                break;
            case Keys.SPACE:
                interact = false;
                if((alternative_keys_progress & 4) == 4) {
                    putDown = false;
                } else {
                    pickUp = false;
                }
                break;
            case Keys.SHIFT_RIGHT:
                alternative_keys_progress &= ~1;
                break;
            case Keys.SHIFT_LEFT:
                alternative_keys_progress &= ~2;
                break;
            case Keys.H:
                disableHud = false;
            default:
                processed = false;
        }
        return processed;
    }

    @Override
    public boolean keyTyped(char character) {
        boolean processed = true;
        switch (character) {
            default:
                processed = false;
        }
        return processed;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

}
