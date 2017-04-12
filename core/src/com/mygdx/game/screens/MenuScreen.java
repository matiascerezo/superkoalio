package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.KoalaGame;
import com.mygdx.game.helpers.AssetManager;

import java.util.Random;

/**
 * Created by Matias on 09/04/2017.
 */

public class MenuScreen implements Screen {

    private Stage stage;
    public KoalaGame game;
    //public Label textTitle;
    private Label.LabelStyle textStyle;
    //private TextButton jugar;
    private TextButton.TextButtonStyle textButtonStyle;
    private OrthographicCamera camera;

    public MenuScreen(final KoalaGame game, boolean reset) {
        this.game = game;

        Gdx.app.log("FirstScreen", "He entrado");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024,768);
        camera.update();

        StretchViewport viewport = new StretchViewport(1024,768, camera);
        stage = new Stage(viewport);

        //Gdx.app.log(stage.getHeight() + " " , stage.getWidth() + "");

        // Afegim el fons
        stage.addActor(new Image(AssetManager.background));
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = AssetManager.font;
        //Afegim el text de titul
        textStyle = new Label.LabelStyle(AssetManager.font, null);
        //jugar = new TextButton("Jugar", textButtonStyle);


        /*Container contJugar = new Container(jugar);
        contJugar.setTransform(true);
        contJugar.center();
        contJugar.setPosition(330, 120);*/

        if (!reset) {

            Image logo = AssetManager.imgLogo;
            logo.setPosition(420, 380);

            Image nombre = AssetManager.imgNombre;
            nombre.setPosition(250,270);

            ImageButton jugar = AssetManager.btnJugar;
            jugar.setPosition(430,130);

            stage.addActor(logo);
            //stage.addActor(container);
            stage.addActor(nombre);
            stage.addActor(jugar);


            //Afegim les accions de escalar: primer es fa gran i després torna a l'estat original ininterrompudament
            //container.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.scaleTo(1.3f, 1.3f, 1.3f), Actions.scaleTo(1, 1, 1))));
            //stage.addActor(containerLogo);

            //Donem funcio als botons.
            jugar.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MenuScreen.this.game.setScreen(new Superkoalio(game));
                }
            });
            Gdx.input.setInputProcessor(stage);
        } else if (reset){
            String[] frases = {"Qué lástima, has muerto.", "Qué pena, ya casi lo tenías...", "¡Vuelve a probar!",
                    "¡A la próxima lo consigues!", "Esa parte es fácil..."};
            Random random = new Random();

            /*textTitle = new Label(frases[posicion], textStyle);
            Container container = new Container(textTitle);
            container.setTransform(true);
            container.center();
            container.setPosition(330,280);*/

            ImageButton reiniciar = AssetManager.btnReiniciar;
            reiniciar.setPosition(370,90);

            //Texture imgMuerte = new Texture("buttons/muerte"+random.nextInt(3)+".png");
            Texture imgMuerte = new Texture("buttons/muerte"+random.nextInt(3)+".png");
            Drawable drawable = new TextureRegionDrawable(new TextureRegion(imgMuerte));
            ImageButton btnMuerte = new ImageButton(drawable);
            btnMuerte.setPosition(310,290);

            //jugar.setText("Reiniciar");
            //contJugar.setActor(jugar);
            //contJugar.setPosition(330,70);

            //textButtonStyle.font.getData().setScale(1.8f);

            stage.addActor(btnMuerte);
            stage.addActor(reiniciar);

            //stage.addActor(contJugar);

            reiniciar.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MenuScreen.this.game.setScreen(new Superkoalio(game));
                }
            });
            Gdx.input.setInputProcessor(stage);
        }
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