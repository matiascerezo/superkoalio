package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.screens.*;
import com.mygdx.game.screens.Superkoalio;

/**
 * Created by Matias on 09/04/2017.
 */

public class KoalaGame extends Game {

    @Override
    public void create() {
        Gdx.app.log("Hola","Estoy aqui");
        AssetManager.load();
        setScreen(new MenuScreen(this,false));
    }

    @Override
    public void dispose() {
        super.dispose();
        AssetManager.dispose();
    }
}
