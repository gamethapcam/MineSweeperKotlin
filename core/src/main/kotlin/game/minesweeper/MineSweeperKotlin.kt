package game.minesweeper

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.kotcrab.vis.ui.VisUI
import game.minesweeper.screens.PlayScreen

class MineSweeperKotlin : Game() {

    internal lateinit var batch: SpriteBatch

    internal lateinit var playScreen: PlayScreen

    override fun create() {
        VisUI.load()

        batch = SpriteBatch()

        playScreen = PlayScreen(batch)
        setScreen(playScreen)
    }

    override fun render() {
        super.render()

    }

    override fun dispose() {
        batch.dispose()
        VisUI.dispose()
    }

}
