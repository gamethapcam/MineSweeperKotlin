package game.minesweeper

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import game.minesweeper.screens.PlayScreen

class MineSweeperKotlin : Game() {

    internal lateinit var batch: SpriteBatch

    internal lateinit var playScreen: PlayScreen

    internal lateinit var stage: Stage
    internal lateinit var fpsLabel: Label
    internal lateinit var ubuntuFont32: BitmapFont

    override fun create() {
        batch = SpriteBatch()

        ubuntuFont32 = BitmapFont(Gdx.files.internal("fonts/Ubuntu32.fnt"))

        fpsLabel = Label("FPS:", Label.LabelStyle(ubuntuFont32, Color.WHITE))
        stage = Stage()
        stage.addActor(fpsLabel)

        playScreen = PlayScreen(batch)
        setScreen(playScreen)
    }

    override fun render() {
        super.render()

        fpsLabel.setText("FPS: ${Gdx.graphics.framesPerSecond}")
        stage.draw()
    }

    override fun dispose() {
        batch.dispose()
        stage.dispose()
        ubuntuFont32.dispose()
    }

}
