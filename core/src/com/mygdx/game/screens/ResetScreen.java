package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.KoalaGame;
import com.mygdx.game.helpers.AssetManager;


/**
 * Created by Matias on 10/04/2017.
 */

public class ResetScreen implements Screen {

    private Stage stage;
    public KoalaGame game;
    public Label textTitle;
    private Label.LabelStyle textStyle;
    public TextButton reiniciar;
    private TextButton.TextButtonStyle textButtonStyle;
    private OrthographicCamera camera;

    public ResetScreen(final KoalaGame game) {
        this.game = game;

        Gdx.app.log("ResetScreen", "He entrado");

        stage = new Stage();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 30, 20);
        camera.update();

        // Afegim el fons
        stage.addActor(new Image(AssetManager.background));
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = AssetManager.font;
        //Afegim el text de titul
        textStyle = new Label.LabelStyle(AssetManager.font, null);


        textTitle = new Label("¡Enhorabuena, has completado el nivel!", textStyle);

        Container container = new Container(textTitle);
        container.setTransform(true);
        container.center();
        container.setPosition(330, 230);

        reiniciar = new TextButton("Reiniciar", textButtonStyle);

        Container contJugar = new Container(reiniciar);
        contJugar.setTransform(true);
        contJugar.center();
        contJugar.setPosition(330, 70);

        textButtonStyle.font.getData().setScale(1.2f);
        textStyle.font.getData().setScale(1.2f);

        stage.addActor(container);
        stage.addActor(contJugar);

        contJugar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ResetScreen.this.game.setScreen(new Superkoalio(game));
            }
        });
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}