package game.minesweeper.screens

import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.FitViewport
import game.minesweeper.ui.Gui
import java.util.*


enum class Mine {
    MINE,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    EMPTY,
}

enum class Status {
    UNSOLVED,
    CLEARED,
    TAGGED_FLAG,
    TAGGED_QUESTION
}

data class MapCoord(val x: Int, val y: Int)

class PlayScreen(val batch: SpriteBatch): Screen, InputProcessor {

    val WIDTH = 40.0f
    val HEIGHT = 30.0f

    val BLOCK_SIZE = 1.5f

    private lateinit var viewport: FitViewport
    private lateinit var camera: OrthographicCamera
    private lateinit var stage: Stage
    private lateinit var flagImage: Image
    private lateinit var flagLabel: Label
    private lateinit var timerImage: Image
    private lateinit var timerLabel: Label
    private lateinit var mineClearedLabel: Label
    private lateinit var gameOverLabel: Label

    private val assetManager = AssetManager()

    val MIN_MAP_WIDTH = 1
    val MAX_MAP_WIDTH = 26
    val MIN_MAP_HEIGHT = 1
    val MAX_MAP_HEIGHT = 16

    var mapWidth = 10    // should be between MIN_MAP_WIDTH and MAX_MAP_WIDTH
    var mapHeight = 10    // should be between MIN_MAP_HEIGHT and MAX_MAP_HEIGHT
    val mapSize: Int
        get() = mapWidth * mapHeight
    var totalMineNumber: Int = mapSize / 10 // should be between 1 and mapSize

    var gameOver = false
    var mineCleared = false
    var flags = totalMineNumber
    var questions = 0
    var unsolved = mapSize

    var timer = false
    var timerCountDown = 999f
    var timerCounter = timerCountDown

    private lateinit var mineMap: Array<Mine>
    private lateinit var mineMapStatus: Array<Status>

    private lateinit var spriteBlock: Sprite
    private lateinit var spriteClearBlock: Sprite
    private lateinit var spriteQuestion: Sprite
    private lateinit var spriteMine: Sprite

    private lateinit var spriteNum1: Sprite
    private lateinit var spriteNum2: Sprite
    private lateinit var spriteNum3: Sprite
    private lateinit var spriteNum4: Sprite
    private lateinit var spriteNum5: Sprite
    private lateinit var spriteNum6: Sprite
    private lateinit var spriteNum7: Sprite
    private lateinit var spriteNum8: Sprite
    private lateinit var spriteFlag: Sprite

    private lateinit var gui: Gui

    override fun show() {
        assetManager.load("fonts/Ubuntu32.fnt", BitmapFont::class.java)
        assetManager.load("img/actors.pack", TextureAtlas::class.java)
        assetManager.finishLoading()

        camera = OrthographicCamera()
        viewport = FitViewport(WIDTH, HEIGHT, camera)

        camera.translate(Vector2(WIDTH / 2f, HEIGHT / 2f))

        val textureAtlas = assetManager.get("img/actors.pack", TextureAtlas::class.java)
        val ubuntu32Font = assetManager.get("fonts/Ubuntu32.fnt", BitmapFont::class.java)
        val labelStyle = Label.LabelStyle(ubuntu32Font, Color.WHITE)
        mineClearedLabel = Label("Cleared!", labelStyle)
        mineClearedLabel.setPosition((Gdx.graphics.width - mineClearedLabel.width) / 2f, (Gdx.graphics.height - mineClearedLabel.height) / 2f)
        mineClearedLabel.isVisible = mineCleared

        gameOverLabel = Label("Game Over", labelStyle)
        gameOverLabel.setPosition((Gdx.graphics.width - gameOverLabel.width) / 2f, (Gdx.graphics.height - gameOverLabel.height) / 2f)
        gameOverLabel.isVisible = gameOver

        flagImage = Image(textureAtlas.findRegion("Flag"))
        flagLabel = Label("$flags", labelStyle)
        flagImage.setPosition(20f, Gdx.graphics.height - 95f)
        flagLabel.setPosition(80f, Gdx.graphics.height - 80f)

        timerImage = Image(textureAtlas.findRegion("Timer"))
        timerLabel = Label("$timerCounter", labelStyle)
        timerImage.setPosition(160f, Gdx.graphics.height - 90f)
        timerLabel.setPosition(240f, Gdx.graphics.height - 80f)
        timerImage.isVisible = timer
        timerLabel.isVisible = timer

        stage = Stage()
        stage.addActor(mineClearedLabel)
        stage.addActor(gameOverLabel)
        stage.addActor(flagImage)
        stage.addActor(flagLabel)
        stage.addActor(timerImage)
        stage.addActor(timerLabel)

        spriteBlock = Sprite(textureAtlas.findRegion("Block"))
        spriteBlock.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)

        spriteClearBlock = Sprite(textureAtlas.findRegion("ClearBlock"))
        spriteClearBlock.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)

        spriteQuestion = Sprite(textureAtlas.findRegion("Question"))
        spriteQuestion.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)

        spriteMine = Sprite(textureAtlas.findRegion("Mine"))
        spriteMine.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)

        spriteNum1 = Sprite(textureAtlas.findRegion("Num1"))
        spriteNum2 = Sprite(textureAtlas.findRegion("Num2"))
        spriteNum3 = Sprite(textureAtlas.findRegion("Num3"))
        spriteNum4 = Sprite(textureAtlas.findRegion("Num4"))
        spriteNum5 = Sprite(textureAtlas.findRegion("Num5"))
        spriteNum6 = Sprite(textureAtlas.findRegion("Num6"))
        spriteNum7 = Sprite(textureAtlas.findRegion("Num7"))
        spriteNum8 = Sprite(textureAtlas.findRegion("Num8"))
        spriteFlag = Sprite(textureAtlas.findRegion("Flag"))

        spriteNum1.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)
        spriteNum2.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)
        spriteNum3.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)
        spriteNum4.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)
        spriteNum5.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)
        spriteNum6.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)
        spriteNum7.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)
        spriteNum8.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)
        spriteFlag.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)

        gui = Gui(this)

        val inputMultiplexer = InputMultiplexer()
        inputMultiplexer.addProcessor(gui.stage)
        inputMultiplexer.addProcessor(this)

        Gdx.input.inputProcessor = inputMultiplexer

        Gdx.gl.glClearColor(0.7f, 0.7f, 0.8f, 1f)

        setupMineMap()
    }

    fun setupMineMap() {
        mineMapStatus = Array(mapSize, {i -> Status.UNSOLVED })
        mineMap = Array(mapSize, {i -> Mine.EMPTY })

        val random = Random()
        for (i in 0..totalMineNumber - 1) {
            var pos = random.nextInt(mapSize)
            while (mineMap[pos] == Mine.MINE) {
                pos = random.nextInt(mapSize)
            }
            mineMap[pos] = Mine.MINE
        }

        for (y in 0..mapHeight - 1) {
            for (x in 0..mapWidth - 1) {
                val pos = y * mapWidth + x
                if (mineMap[pos] != Mine.MINE) {
                    var count = 0
                    for (i in 0..8) {
                        if (i == 4) {
                            continue
                        }
                        val cx = x + (i % 3 - 1)
                        val cy= y + (i / 3 - 1)
                        if (cx >=0 && cx < mapWidth && cy >= 0 && cy < mapHeight) {
                            if (mineMap[cy * mapWidth + cx] == Mine.MINE) {
                                count++
                            }
                        }
                    }

                    mineMap[pos] = when (count) {
                        1 -> Mine.ONE
                        2 -> Mine.TWO
                        3 -> Mine.THREE
                        4 -> Mine.FOUR
                        5 -> Mine.FIVE
                        6 -> Mine.SIX
                        7 -> Mine.SEVEN
                        8 -> Mine.EIGHT
                        else -> Mine.EMPTY
                    }
                }
            }
        }

    }

    /**
     * print map in console for debugging use
     */
    fun printMap() {

        var count = 0

        mineMap.forEach {
            if (count >= mapWidth) {
                println()
                count = 0
            }
            when(it) {
                Mine.ONE -> print(1)
                Mine.TWO -> print(2)
                Mine.THREE -> print(3)
                Mine.FOUR -> print(4)
                Mine.FIVE -> print(5)
                Mine.SIX -> print(6)
                Mine.SEVEN -> print(7)
                Mine.EIGHT -> print(8)
                Mine.MINE -> print("M")
                else -> print("-")
            }
            count++
        }
    }

    fun restart() {
        setupMineMap()
        flags = totalMineNumber
        unsolved = mapSize
        gameOver = false
        mineCleared = false

        timerCounter = Math.max(0f, timerCountDown)
    }

    override fun pause() {
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun hide() {
    }

    fun update(delta: Float) {
        if (!gameOver && timer) {
            timerCounter -= delta
        }

        if (timerCounter < 0) {
            gameOver = true
        }

        gui.update(delta)
    }

    override fun render(delta: Float) {
        update(delta)

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = camera.combined

        batch.begin()

        for ((index, value) in mineMapStatus.withIndex()) {
            val x = WIDTH / 2f - (mapWidth / 2f - (index % mapWidth)) * BLOCK_SIZE
            val y = (HEIGHT - 6f) / 2f + (mapHeight / 2f - index / mapWidth - 1) * BLOCK_SIZE


            if (value == Status.UNSOLVED) {
                spriteBlock.setPosition(x, y)
                spriteBlock.draw(batch)

                if (gameOver && !mineCleared && mineMap[index] == Mine.MINE) {
                    spriteMine.setPosition(x, y)
                    spriteMine.draw(batch)
                }
            }
            else if (value == Status.TAGGED_FLAG) {
                spriteBlock.setPosition(x, y)
                spriteBlock.draw(batch)
                spriteFlag.setPosition(x, y)
                spriteFlag.draw(batch)
            }
            else if (value == Status.TAGGED_QUESTION) {
                spriteBlock.setPosition(x, y)
                spriteBlock.draw(batch)

                if (gameOver && !mineCleared && mineMap[index] == Mine.MINE) {
                    spriteMine.setPosition(x, y)
                    spriteMine.draw(batch)
                }
                spriteQuestion.setPosition(x, y)
                spriteQuestion.draw(batch)
            }
            else if (value == Status.CLEARED) {
                spriteClearBlock.setPosition(x, y)
                spriteClearBlock.draw(batch)

                when (mineMap[index]) {
                    Mine.ONE -> {
                        spriteNum1.setPosition(x, y)
                        spriteNum1.draw(batch)
                    }

                    Mine.TWO -> {
                        spriteNum2.setPosition(x, y)
                        spriteNum2.draw(batch)
                    }

                    Mine.THREE -> {
                        spriteNum3.setPosition(x, y)
                        spriteNum3.draw(batch)
                    }

                    Mine.FOUR -> {
                        spriteNum4.setPosition(x, y)
                        spriteNum4.draw(batch)
                    }

                    Mine.FIVE -> {
                        spriteNum5.setPosition(x, y)
                        spriteNum5.draw(batch)
                    }

                    Mine.SIX -> {
                        spriteNum6.setPosition(x, y)
                        spriteNum6.draw(batch)
                    }

                    Mine.SEVEN -> {
                        spriteNum7.setPosition(x, y)
                        spriteNum7.draw(batch)
                    }

                    Mine.EIGHT -> {
                        spriteNum8.setPosition(x, y)
                        spriteNum8.draw(batch)
                    }

                    Mine.MINE -> {
                        spriteMine.setPosition(x, y)
                        spriteMine.draw(batch)
                    }

                    else -> {

                    }

                }
            }

        }
        batch.end()

        flagLabel.setText("$flags")
        timerImage.isVisible = timer
        timerLabel.isVisible = timer
        if (timer) {
            timerLabel.setText("%.1f".format(timerCounter))
        }
        mineClearedLabel.isVisible = mineCleared
        gameOverLabel.isVisible = gameOver && !mineCleared
        stage.draw()

        gui.draw()
    }

    override fun resume() {
    }

    override fun dispose() {
        assetManager.dispose()
        gui.dispose()
        stage.dispose()
    }

    private fun checkAllFlaggedCorrectly(): Boolean {
        if (flags > 0) {
            return false
        }

        var allCorrect = true

        for ((index, status) in mineMapStatus.withIndex()) {
            if (status == Status.TAGGED_FLAG && mineMap[index] != Mine.MINE) {
                allCorrect = false
                break
            }
        }

        return allCorrect
    }

    private fun checkMineCleared() {
        unsolved = mineMapStatus
                .filter { e -> e == Status.UNSOLVED || e == Status.TAGGED_QUESTION }
                .count()

        var allCleared = true
        for ((index, status) in mineMapStatus.withIndex()) {

            if ((mineMap[index] != Mine.MINE) &&
                    (status == Status.TAGGED_FLAG || status == Status.TAGGED_QUESTION || status == Status.UNSOLVED)) {
                allCleared = false
                break
            }
        }

        if (allCleared || (questions == 0 && checkAllFlaggedCorrectly())) {
            mineCleared = true
        }

        gameOver = mineCleared
    }

    private fun translateWorldCoordToMapCoord(posX: Float, posY: Float): MapCoord {
        val left = WIDTH / 2f - mapWidth / 2f * BLOCK_SIZE
        val top = (HEIGHT - 6f) / 2f - (mapHeight / 2f) * BLOCK_SIZE

        val x = (posX - left) / BLOCK_SIZE
        val y = mapHeight + (top - posY) / BLOCK_SIZE

        val coordX = if (x >= 0 && x <= mapWidth) x.toInt() else -1
        val coordY = if (y >= 0 && y <= mapHeight) y.toInt() else -1
        return MapCoord(coordX, coordY)
    }


    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val vec3 = Vector3(screenX.toFloat(), screenY.toFloat(), 0f)
        camera.unproject(vec3)
        val (coordX, coordY) = translateWorldCoordToMapCoord(vec3.x, vec3.y)
        val index = coordY * mapWidth + coordX

        when (button) {
            Input.Buttons.LEFT -> {
                if (!gameOver) {
                    // clicked on mine map
                    if (coordX >= 0 && coordY >= 0) {
                        when (mineMapStatus[index]) {
                            Status.UNSOLVED -> {
                                val mine = mineMap[index]

                                if (mine == Mine.MINE) {
                                    gameOver = true
                                } else {
                                    if (mine == Mine.EMPTY) {
                                        checkSurroundingBlocks(coordX, coordY)
                                    } else {
                                        mineMapStatus[index] = Status.CLEARED
                                    }

                                    checkMineCleared()
                                }
                            }
                            else -> {
                            }
                        }
                    }
                }
            }

            Input.Buttons.RIGHT -> {

                if (!gameOver) {
                    // clicked on mine map
                    if (coordX >= 0 && coordY >= 0) {

                        when (mineMapStatus[index]) {
                            Status.UNSOLVED -> {
                                if (flags > 0) {
                                    mineMapStatus[index] = Status.TAGGED_FLAG
                                    flags--
                                    checkMineCleared()
                                }
                            }

                            Status.TAGGED_FLAG -> {
                                mineMapStatus[index] = Status.TAGGED_QUESTION
                                flags++
                                questions++
                            }

                            Status.TAGGED_QUESTION -> {
                                mineMapStatus[index] = Status.UNSOLVED
                                questions--
                                checkMineCleared()
                            }
                            else -> {
                            }
                        }

                    }
                }
            }
        }

        return false
    }

    private fun checkSurroundingBlocks(x: Int, y: Int) {
        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight) {
            return
        }

        val mine = mineMap[y * mapWidth + x]
        val status = mineMapStatus[y * mapWidth + x]
        if (status == Status.UNSOLVED) {
            mineMapStatus[y * mapWidth + x] = Status.CLEARED
            if (mine == Mine.EMPTY) {
                for (i in 0..8) {
                    if (i != 4) {
                        checkSurroundingBlocks(x + (i % 3) - 1, y + (i / 3) - 1)
                    }
                }
            }
        }
    }

    override fun scrolled(amount: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        return false
    }
}
