package game.minesweeper.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Disposable
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.Validators
import com.kotcrab.vis.ui.widget.*
import game.minesweeper.screens.PlayScreen

class Gui(val playScreen: PlayScreen): Disposable {

    val stage: Stage
    val restartButton: VisTextButton
    val settingsButton: VisTextButton

    val settingsWindow: Window

    val mapWidthSlider: VisSlider
    val mapHeightSlider: VisSlider

    val mapWidthNumLabel: VisLabel
    val mapHeightNumLabel: VisLabel

    val totalMineNumberSlider: VisSlider
    val totalMineNumberLabel: VisLabel

    val countDownCheckBox: VisCheckBox
    val timerValidatableTextField: VisValidatableTextField


    init {
        val mapWidthLabel = VisLabel("Board Width:")
        val mapHeightLabel = VisLabel("Board Height:")

        mapWidthSlider = VisSlider(playScreen.MIN_MAP_WIDTH.toFloat(), playScreen.MAX_MAP_WIDTH.toFloat(), 1f, false)
        mapHeightSlider = VisSlider(playScreen.MIN_MAP_HEIGHT.toFloat(), playScreen.MAX_MAP_HEIGHT.toFloat(), 1f, false)
        mapWidthSlider.setValue(playScreen.mapWidth.toFloat())
        mapHeightSlider.setValue(playScreen.mapHeight.toFloat())

        mapWidthNumLabel = VisLabel("${playScreen.mapWidth}")
        mapHeightNumLabel = VisLabel("${playScreen.mapHeight}")

        val mineNumberLabel = VisLabel("Total mines:")
        totalMineNumberSlider = VisSlider(1f, playScreen.mapSize.toFloat(), 1f, false)
        totalMineNumberLabel = VisLabel("${playScreen.totalMineNumber}")

        mapWidthSlider.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor?) {
                mapWidthNumLabel.setText("${mapWidthSlider.value.toInt()}")
                totalMineNumberSlider.setRange(1f, mapWidthSlider.value * mapHeightSlider.value)
            }
        })

        mapHeightSlider.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor?) {
                mapHeightNumLabel.setText("${mapHeightSlider.value.toInt()}")
                totalMineNumberSlider.setRange(1f, mapWidthSlider.value * mapHeightSlider.value)
            }
        })

        totalMineNumberSlider.value = playScreen.totalMineNumber.toFloat()
        totalMineNumberSlider.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor?) {
                totalMineNumberLabel.setText("${totalMineNumberSlider.value.toInt()}")
            }
        })

        timerValidatableTextField = VisValidatableTextField("${playScreen.timerCountDown}")
        countDownCheckBox = VisCheckBox("Count down")
        countDownCheckBox.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                timerValidatableTextField.isDisabled = !countDownCheckBox.isChecked
            }
        })
        timerValidatableTextField.isDisabled = !countDownCheckBox.isChecked
        timerValidatableTextField.addValidator(Validators.FloatValidator())
        timerValidatableTextField.isRestoreLastValid = true
        val timerLabel = VisLabel("seconds")

        val windowWidth = 480f
        val windowHeight = 280f
        settingsWindow = Window("Settings", VisUI.getSkin())
        settingsWindow.isVisible = false
        settingsWindow.setSize(windowWidth, windowHeight)
        settingsWindow.setPosition((Gdx.graphics.width - windowWidth) / 2f, (Gdx.graphics.height - windowHeight) / 2f)
        val applyButton = VisTextButton("Apply")
        val cancelButton = VisTextButton("Cancel")

        applyButton.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                playScreen.mapWidth = mapWidthSlider.value.toInt()
                playScreen.mapHeight = mapHeightSlider.value.toInt()
                playScreen.totalMineNumber = totalMineNumberSlider.value.toInt()

                playScreen.timer = countDownCheckBox.isChecked
                playScreen.timerCountDown = if (timerValidatableTextField.isInputValid) timerValidatableTextField.text.toFloat() else playScreen.timerCountDown
                playScreen.restart()
                settingsWindow.isVisible = false
            }
        })

        cancelButton.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                settingsWindow.isVisible = false
            }
        })

        val settingsTable = VisTable()
        settingsTable.setFillParent(true)
        settingsTable.top().left().padTop(64f)
        settingsTable.add(mapWidthLabel)
        settingsTable.add(mapWidthSlider).pad(6f)
        settingsTable.add(mapWidthNumLabel)
        settingsTable.row()
        settingsTable.add(mapHeightLabel)
        settingsTable.add(mapHeightSlider).pad(6f)
        settingsTable.add(mapHeightNumLabel)
        settingsTable.row()
        settingsTable.add(mineNumberLabel)
        settingsTable.add(totalMineNumberSlider).pad(6f)
        settingsTable.add(totalMineNumberLabel)
        settingsTable.row()
        settingsTable.add(countDownCheckBox).pad(6f)
        settingsTable.add(timerValidatableTextField)
        settingsTable.add(timerLabel)
        settingsTable.row()
        settingsTable.add(applyButton).padTop(16f).padRight(10f)
        settingsTable.add(cancelButton).padTop(16f)
        settingsWindow.add(settingsTable)

        restartButton = VisTextButton("Restart")
        restartButton.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                playScreen.restart()
                settingsWindow.isVisible = false
            }
        })

        settingsButton = VisTextButton("Settings")
        settingsButton.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                settingsWindow.isVisible = true
            }
        })

        val table = VisTable()
        table.setFillParent(true)
        table.top().left().pad(6f)
        table.add(restartButton).padRight(6f)
        table.add(settingsButton)

        stage = Stage()
        stage.addActor(table)
        stage.addActor(settingsWindow)
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
