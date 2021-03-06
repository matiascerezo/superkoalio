package com.mygdx.game.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import javax.annotation.Resources;

public class AssetManager {

    // Sprite Sheet
    public static Texture sheet;
    public static TextureRegion background;
    public static TextureRegion[] regions;
    public static Texture koalaTexture;
    public static Image imgLogo, imgNombre;
    public static Animation<TextureRegion> stand;
    public static Animation<TextureRegion> walk;
    public static Animation<TextureRegion> jump;
    public static ImageButton btnJugar, btnReiniciar, btnVictoria, btnNivelOk;

    public static Drawable background1;

    // Sons
    public static Sound jumpSound, winSound, fallSound, teletranspSound;
    public static Music music;

    // Font
    public static BitmapFont font;

    public static void load() {

        // Carreguem les textures
        sheet = new Texture(Gdx.files.internal("sheet2.jpg"));
        sheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);


        // Sprites del Koala
        koalaTexture = new Texture("koalio.png");
        imgLogo = new Image(new Texture("logo.png"));
        imgNombre = new Image(new Texture("buttons/SuperKoalio.png"));

        Texture imgJugar = new Texture("buttons/Jugar.png");
        Drawable drawable = new TextureRegionDrawable(new TextureRegion(imgJugar));
        btnJugar = new ImageButton(drawable);

        Texture imgReiniciar = new Texture("buttons/Reiniciar.png");
        Drawable drawable1 = new TextureRegionDrawable(new TextureRegion(imgReiniciar));
        btnReiniciar = new ImageButton(drawable1);

        Texture imgVictoria = new Texture("buttons/Enhorabuena.png");
        Drawable drawable2 = new TextureRegionDrawable(new TextureRegion(imgVictoria));
        btnVictoria = new ImageButton(drawable2);

        Texture imgLevel = new Texture("buttons/FullLevel.png");
        Drawable drawable3 = new TextureRegionDrawable(new TextureRegion(imgLevel));
        btnNivelOk = new ImageButton(drawable3);

        regions = TextureRegion.split(koalaTexture, 18, 26)[0];

        //Background
        background = new TextureRegion(sheet,3,3,1022,768);
        //background = new TextureRegion(sheet,0,0,700,390);
        background.flip(false,false);

        background1 = new Image(new Texture("background1.png")).getDrawable();

        //Animacions
        stand = new Animation(0, regions[0]);
        jump = new Animation(0, regions[1]);
        walk = new Animation(0.15f, regions[2], regions[3], regions[4]);
        walk.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        /******************************* Sounds *************************************/
        //Musica teletransporte
        teletranspSound = Gdx.audio.newSound(Gdx.files.internal("sounds/teletransp.wav"));

        //Musica victoria
        winSound = Gdx.audio.newSound((Gdx.files.internal("sounds/victoria.mp3")));

        //Fall sound
        fallSound = Gdx.audio.newSound(Gdx.files.internal("sounds/caer1.mp3"));

        //Sonido salto
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/salto.mp3"));

        // Música del joc
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
        music.setVolume(0.2f);
        music.setLooping(true);

        /******************************* Text *************************************/
        // Font space
        /*FileHandle fontFile = Gdx.files.internal("fonts/fuente.fnt");
        font = new BitmapFont(fontFile, false);
        font.getData().setScale(1.8f);*/
    }

    public static void dispose() {
        // Descartem els recursos
        music.dispose();
        fallSound.dispose();
        winSound.dispose();
        jumpSound.dispose();
        teletranspSound.dispose();
    }
}
