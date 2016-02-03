package game.minesweeper.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Disposable
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import game.minesweeper.screens.PlayScreen

class Gui(val playScreen: PlayScreen): Disposable {

    val stage: Stage
    val restartButton: VisTextButton
    val settingsButton: VisTextButton

    init {
        restartButton = VisTextButton("Restart")
        restartButton.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                playScreen.restart()
            }
        })

        settingsButton = VisTextButton("Settings")
        settingsButton.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                println("settings clicked")
            }
        })

        val table = VisTable()
        table.setFillParent(true)
        table.top().left().pad(6f)
        table.add(restartButton).padRight(6f)
        table.add(settingsButton)

        stage = Stage()
        stage.addActor(table)
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
