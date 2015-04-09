package pb.tic_tac_toe;

/**
 * Created by per on 09/04/15.
 */
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

public class TicTacToeActivity extends SimpleBaseGameActivity implements ButtonSprite.OnClickListener {

    final private int CAMERA_WIDTH = 480;
    final private int CAMERA_HEIGHT = 480;

    final private int GRID_WIDTH = 3;
    final private int GRID_HEIGHT = 3;

    final private float STROKE_WIDTH = 4;

    private BuildableBitmapTextureAtlas mBitmapTextureAtlas;
    private ITextureRegion mBlankTextureRegion;
    private ITextureRegion mXTextureRegion;
    private ITextureRegion mOTextureRegion;

    private ButtonSprite[][] gridSprite = new ButtonSprite[GRID_WIDTH][GRID_HEIGHT];

    private Model board = new Model();
    private Piece currentPlayer = board.getCurrentPlayer();

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
    }

    @Override
    protected void onCreateResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        this.mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 256, 256);
        this.mBlankTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "blankicon.png");
        this.mXTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "xicon.png");
        this.mOTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "oicon.png");

        try {
            this.mBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
            this.mBitmapTextureAtlas.load();
        } catch (ITextureAtlasBuilder.TextureAtlasBuilderException e) {
            Debug.e(e);
        }
    }

    @Override
    protected Scene onCreateScene() {
        final Scene scene = new Scene();
        final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();

        float lineX[] = new float[GRID_WIDTH];
        float lineY[] = new float[GRID_HEIGHT];

        float touchX[] = new float[GRID_WIDTH];
        float touchY[] = new float[GRID_HEIGHT];

        float midTouchX = CAMERA_WIDTH / GRID_WIDTH / 2;
        float midTouchY = CAMERA_HEIGHT / GRID_HEIGHT / 2;

        float halfTouchX = mBlankTextureRegion.getWidth() / 2;
        float halfTouchY = mBlankTextureRegion.getHeight() / 2;

        float paddingX = midTouchX - halfTouchX;
        float paddingY = midTouchY - halfTouchY;

        for (int i = 0; i < GRID_WIDTH; i++) {
            lineX[i] = CAMERA_WIDTH / GRID_WIDTH * i;
            touchX[i] = lineX[i] + paddingX;
        }

        for (int i = 0; i < GRID_HEIGHT; i++) {
            lineY[i] = CAMERA_HEIGHT / GRID_HEIGHT * i;
            touchY[i] = lineY[i] + paddingY;
        }

        scene.setBackground(new Background(0.85f, 0.85f, 0.85f));

        // draw the grid lines
        for (int i = 0; i < GRID_WIDTH; i++) {
            final Line line = new Line(lineX[i], 0, lineX[i], CAMERA_HEIGHT, STROKE_WIDTH, vertexBufferObjectManager);
            line.setColor(0.15f, 0.15f, 0.15f);
            scene.attachChild(line);
        }
        for (int i = 0; i < GRID_HEIGHT; i++) {
            final Line line = new Line(0, lineY[i], CAMERA_WIDTH, lineY[i], STROKE_WIDTH, vertexBufferObjectManager);
            line.setColor(0.15f, 0.15f, 0.15f);
            scene.attachChild(line);
        }

        // button sprites
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                final ButtonSprite button = new ButtonSprite(touchX[i], touchY[j], this.mBlankTextureRegion, this.mXTextureRegion, this.mOTextureRegion, this.getVertexBufferObjectManager(), this);
                scene.registerTouchArea(button);
                scene.attachChild(button);
                gridSprite[i][j] = button;
            }
        }

        scene.setTouchAreaBindingOnActionDownEnabled(true);

        return scene;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        EngineOptions options = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
        return options;
    }

    @Override
    public void onClick(final ButtonSprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // determine which button was pressed
                float x = pButtonSprite.getX();
                float y = pButtonSprite.getY();

                int gridX = (int)Math.floor(x / CAMERA_WIDTH * GRID_WIDTH);
                int gridY = (int)Math.floor(y / CAMERA_HEIGHT* GRID_HEIGHT);

                if (gridSprite[gridX][gridY] == pButtonSprite && currentPlayer == board.getCurrentPlayer()) {
                    // update the data model
                    board.setValue(gridX, gridY, currentPlayer);

                    // disable the button
                    pButtonSprite.setEnabled(false);

                    if (currentPlayer == Piece.X) {
                        // change the sprite to x
                        pButtonSprite.setCurrentTileIndex(1);
                    } else {
                        // change the sprite to o
                        pButtonSprite.setCurrentTileIndex(2);
                    }

                    // check if there's a winner
                    Piece winner = board.checkWinner();
                    if (winner != Piece._) {
                        // pop up a dialog if there's a winner
                        AlertDialog.Builder ADBuilder = new AlertDialog.Builder(TicTacToeActivity.this);
                        ADBuilder.setMessage(winner + " wins.").show();
                        reset();
                        board.reset();
                    } else if (board.checkIfGameIsOver()) {
                        AlertDialog.Builder ADBuilder = new AlertDialog.Builder(TicTacToeActivity.this);
                        ADBuilder.setMessage("no winner, restarting...").show();
                        reset();
                        board.reset();
                    }

                    currentPlayer = board.getCurrentPlayer();
                }
            }
        });
    }

    private void reset() {
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                gridSprite[i][j].setEnabled(true);
                gridSprite[i][j].setCurrentTileIndex(0);
            }
        }
    }
}
