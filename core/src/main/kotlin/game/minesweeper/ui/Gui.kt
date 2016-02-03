package game.minesweeper.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Disposable
import com.kotcrab.vis.ui.widget.VisTextButton
import game.minesweeper.screens.PlayScreen

class Gui(val playScreen: PlayScreen): Disposable {

    val stage: Stage
    val restartButton: VisTextButton

    init {
        restartButton = VisTextButton("Restart")
        restartButton.setPosition(6f, 480f - 32f)
        restartButton.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                playScreen.restart()
            }
        })


        stage = Stage()
        stage.addActor(restartButton)
    }

    fun update(delta: Float) {
        stage.act(delta)
    }

    fun draw() {
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }
}
