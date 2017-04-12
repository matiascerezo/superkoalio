package com.mygdx.game.screens;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.KoalaGame;
import com.mygdx.game.helpers.AssetManager;

/**
 * Super Mario Brothers-like very basic platformer, using a tile map built using <a href="http://www.mapeditor.org/">Tiled</a> and a
 * tileset and sprites by <a href="http://www.vickiwenderlich.com/">Vicky Wenderlich</a></p>
 * <p>
 * Shows simple platformer collision detection as well as on-the-fly map modifications through destructible blocks!
 *
 * @author mzechner
 */
public class Superkoalio implements Screen {
    /**
     * The player character, has state and state time,
     */

    static class Settings {

        enum State {
            Standing, Walking, Jumping
        }

        final Vector2 position = new Vector2();
        final Vector2 velocity = new Vector2();
        State state = State.Walking;
        float stateTime = 0;
        boolean facesRight = true;
        boolean grounded = false;
    }

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Animation<TextureRegion> stand;
    private Animation<TextureRegion> walk;
    private Animation<TextureRegion> jump;
    public Texture koalaTexture;
    private TextureRegion[] regions;
    public Settings koala;
    public KoalaGame game;
    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    private Array<Rectangle> tiles = new Array<Rectangle>();
    private Music music;
    Boolean caida = false;

    private static final float GRAVITY = -2.5f;

    private boolean debug = false;
    private boolean muerte = true;
    private ShapeRenderer debugRenderer;

    public Superkoalio(KoalaGame game) {

        Gdx.app.log("SuperKoalio", "Iniciar");
        this.game = game;
        //Iniciamos el juego
        iniciarJuego();
    }

    private void iniciarJuego() {
        koalaTexture = AssetManager.koalaTexture;
        regions = AssetManager.regions;
        stand = AssetManager.stand;
        jump = AssetManager.jump;
        walk = AssetManager.walk;

        music = AssetManager.music;

        muerte = true;
        //Iniciamos la musica.
        music.play();

        // figure out the width and height of the koala for collision
        // detection and rendering by converting a koala frames pixel
        // size into world units (1 unit == 16 pixels)
        com.mygdx.game.utils.Settings.WIDTH = 1 / 16f * regions[0].getRegionWidth();
        com.mygdx.game.utils.Settings.HEIGHT = 1 / 16f * regions[0].getRegionHeight();

        // load the map, set the unit scale to 1/16 (1 unit == 16 pixels)
        //Cargamos el mapa.
        map = new TmxMapLoader().load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);

        // create an orthographic camera, shows us 30x20 units of the world
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 35, 30);
        camera.update();
        koala = new Settings();
        // create the Koala we want to move around the world
        koala.position.set(25, 12);
        //koala.position.set(330,12);
        debugRenderer = new ShapeRenderer();
    }


    private void updateKoala(float deltaTime) {
        if (deltaTime == 0) return;

        if (deltaTime > 0.1f)
            deltaTime = 0.1f;

        koala.stateTime += deltaTime;

        // check input and apply to velocity & state
        //Para saltar
        if ((Gdx.input.isKeyPressed(Keys.SPACE) || isTouched(0.5f, 1)) && koala.grounded) {
            koala.velocity.y += com.mygdx.game.utils.Settings.JUMP_VELOCITY;
            koala.state = Settings.State.Jumping;
            koala.grounded = false;
            Sound salto = AssetManager.jumpSound;
            salto.play();
        }

        //Para ir a la izquierda
        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A) || isTouched(0, 0.25f)) {
            koala.velocity.x = -com.mygdx.game.utils.Settings.MAX_VELOCITY;
            if (koala.grounded) koala.state = Settings.State.Walking;
            koala.facesRight = false;
        }

        //Para ir a la derecha
        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D) || isTouched(0.25f, 0.5f)) {
            koala.velocity.x = com.mygdx.game.utils.Settings.MAX_VELOCITY;
            if (koala.grounded) koala.state = Settings.State.Walking;
            koala.facesRight = true;
        }

        //Para modo debug.
        if (Gdx.input.isKeyJustPressed(Keys.B))
            debug = !debug;

        // apply gravity if we are falling
        koala.velocity.add(0, GRAVITY);

        // clamp the velocity to the maximum, x-axis only
        koala.velocity.x = MathUtils.clamp(koala.velocity.x,
                -com.mygdx.game.utils.Settings.MAX_VELOCITY, com.mygdx.game.utils.Settings.MAX_VELOCITY);

        // If the velocity is < 1, set it to 0 and set state to Standing
        if (Math.abs(koala.velocity.x) < 1) {
            koala.velocity.x = 0;
            if (koala.grounded) koala.state = Settings.State.Standing;
        }

        // multiply by delta time so we know how far we go
        // in this frame
        koala.velocity.scl(deltaTime);

        // perform collision detection & response, on each axis, separately
        // if the koala is moving right, check the tiles to the right of it's
        // right bounding box edge, otherwise check the ones to the left
        Rectangle koalaRect = rectPool.obtain();
        koalaRect.set(koala.position.x, koala.position.y, com.mygdx.game.utils.Settings.WIDTH, com.mygdx.game.utils.Settings.HEIGHT);
        int startX, startY, endX, endY;
        if (koala.velocity.x > 0) {
            startX = endX = (int) (koala.position.x + com.mygdx.game.utils.Settings.WIDTH + koala.velocity.x);
        } else {
            startX = endX = (int) (koala.position.x + koala.velocity.x);
        }
        startY = (int) (koala.position.y);
        endY = (int) (koala.position.y + com.mygdx.game.utils.Settings.HEIGHT);
        getTiles(startX, startY, endX, endY, tiles);
        koalaRect.x += koala.velocity.x;
        for (Rectangle tile : tiles) {
            if (koalaRect.overlaps(tile)) {
                koala.velocity.x = 0;
                break;
            }
        }
        koalaRect.x = koala.position.x;

        // if the koala is moving upwards, check the tiles to the top of its
        // top bounding box edge, otherwise check the ones to the bottom
        if (koala.velocity.y > 0) {
            startY = endY = (int) (koala.position.y + com.mygdx.game.utils.Settings.HEIGHT + koala.velocity.y);
        } else {
            startY = endY = (int) (koala.position.y + koala.velocity.y);
        }
        startX = (int) (koala.position.x);
        endX = (int) (koala.position.x + com.mygdx.game.utils.Settings.WIDTH);
        getTiles(startX, startY, endX, endY, tiles);
        koalaRect.y += koala.velocity.y;
        for (Rectangle tile : tiles) {
            if (koalaRect.overlaps(tile)) {
                // we actually reset the koala y-position here
                // so it is just below/above the tile we collided with
                // this removes bouncing :)
                if (koala.velocity.y > 0) {
                    koala.position.y = tile.y - com.mygdx.game.utils.Settings.HEIGHT;

                    // we hit a block jumping upwards, let's destroy it!
                    //TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("walls");
                    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("destroyblocks");
                    layer.setCell((int) tile.x, (int) tile.y, null);
                } else {
                    koala.position.y = tile.y + tile.height;
                    // if we hit the ground, mark us as grounded so we can jump
                    koala.grounded = true;
                }
                koala.velocity.y = 0;
                break;
            }
        }
        rectPool.free(koalaRect);

        //Gdx.app.log("Posicion X", koala.position.x + "");

        // unscale the velocity by the inverse delta time and set
        // the latest position
        koala.position.add(koala.velocity);
        koala.velocity.scl(1 / deltaTime);

        // Apply damping to the velocity on the x-axis so we don't
        // walk infinitely once a key was pressed
        koala.velocity.x *= com.mygdx.game.utils.Settings.DAMPING;

        //Suena la caida y setea la screen.
        if (koala.position.y <= 0 && muerte) {
            Gdx.app.log("KoalaGame", "Has muerto");
            Sound sonidoCaer = AssetManager.fallSound;
            music.stop();
            sonidoCaer.play();
            muerte = false;

            game.setScreen(new MenuScreen(game, true));
            //iniciarJuego();
        }
        //Cuando gana el koala(llega a la manzana) suena un sonido de victoria, se para la musica
        //del juego y se cambia la screen.
        if (koala.position.x >= 337 && muerte && koala.position.y <= 11) {
            Gdx.app.log("KoalaGame", "Has ganado");
            muerte = false;
            Sound sonidoVictoria = AssetManager.winSound;
            music.stop();
            sonidoVictoria.play();
            game.setScreen(new ResetScreen(game));
        }
        //Para teletransportarse
        if (koala.position.x >= 127 && koala.position.x <= 130 && koala.position.y <= 2) {
            koala.position.x = 153;
            koala.position.y = 30;
            camera.position.y = 22;
            caida = true;
            Sound teletransp = AssetManager.teletranspSound;
            teletransp.play();
        }
        //Para resetear la camara cuando cae de la montañita cuando se teletransporta.
        if (caida && koala.position.y < 18) {
            camera.position.y = 15;
            caida = false;
        }
        //Gdx.app.log("Posicion X", koala.position.x + "" + " Posicion Y " + koala.position.y);
    }

    private boolean isTouched(float startX, float endX) {
        // Check for touch inputs between startX and endX
        // startX/endX are given between 0 (left edge of the screen) and 1 (right edge of the screen)
        for (int i = 0; i < 2; i++) {
            float x = Gdx.input.getX(i) / (float) Gdx.graphics.getWidth();
            if (Gdx.input.isTouched(i) && (x >= startX && x <= endX)) {
                return true;
            }
        }
        return false;
    }

    private void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("walls");
        rectPool.freeAll(tiles);
        tiles.clear();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x, y, 1, 1);
                    tiles.add(rect);
                }
            }
        }
    }

    private void renderKoala(float deltaTime) {
        // based on the koala state, get the animation frame
        TextureRegion frame = null;
        switch (koala.state) {
            case Standing:
                frame = stand.getKeyFrame(koala.stateTime);
                break;
            case Walking:
                frame = walk.getKeyFrame(koala.stateTime);
                break;
            case Jumping:
                frame = jump.getKeyFrame(koala.stateTime);
                break;
        }

        // draw the koala, depending on the current velocity
        // on the x-axis, draw the koala facing either right
        // or left
        Batch batch = renderer.getBatch();
        batch.begin();
        if (koala.facesRight) {
            batch.draw(frame, koala.position.x, koala.position.y, com.mygdx.game.utils.Settings.WIDTH, com.mygdx.game.utils.Settings.HEIGHT);
        } else {
            batch.draw(frame, koala.position.x + com.mygdx.game.utils.Settings.WIDTH, koala.position.y, -com.mygdx.game.utils.Settings.WIDTH, com.mygdx.game.utils.Settings.HEIGHT);
        }
        batch.end();
    }

    private void renderDebug() {
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeType.Line);

        debugRenderer.setColor(Color.RED);
        debugRenderer.rect(koala.position.x, koala.position.y, com.mygdx.game.utils.Settings.WIDTH, com.mygdx.game.utils.Settings.HEIGHT);

        debugRenderer.setColor(Color.YELLOW);
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("walls");
        for (int y = 0; y <= layer.getHeight(); y++) {
            for (int x = 0; x <= layer.getWidth(); x++) {
                Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    if (camera.frustum.boundsInFrustum(x + 0.5f, y + 0.5f, 0, 1, 1, 0))
                        debugRenderer.rect(x, y, 1, 1);
                }
            }
        }
        debugRenderer.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // clear the screen
        Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // get the delta time
        float deltaTime = Gdx.graphics.getDeltaTime();

        // update the koala (process input, collision detection, position update)
        updateKoala(deltaTime);

        // let the camera follow the koala, x-axis only
        camera.position.x = koala.position.x;
        //camera.position.y = koala.position.y;
        camera.update();

        // set the TiledMapRenderer view based on what the
        // camera sees, and render the map
        renderer.setView(camera);
        renderer.render();

        // render the koala
        renderKoala(deltaTime);

        // render debug rectangles
        if (debug) renderDebug();
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