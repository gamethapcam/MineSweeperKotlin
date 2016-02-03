package game.minesweeper.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
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

    private val assetManager = AssetManager()

    private var mapWidth = 10    // should not exceed 26
    private var mapHeight = 10    // should not exceed 16
    private var mapSize = mapWidth * mapHeight
    private var totalMineNumber: Int = mapSize / 10

    private lateinit var mineMap: Array<Mine>
    private lateinit var mineMapStatus: Array<Status>

    private lateinit var spriteBlock: Sprite
    private lateinit var spriteClearBlock: Sprite

    private lateinit var spriteNum1: Sprite
    private lateinit var spriteNum2: Sprite
    private lateinit var spriteNum3: Sprite
    private lateinit var spriteNum4: Sprite
    private lateinit var spriteNum5: Sprite
    private lateinit var spriteNum6: Sprite
    private lateinit var spriteNum7: Sprite
    private lateinit var spriteNum8: Sprite
    private lateinit var spriteFlag: Sprite


    override fun show() {
        assetManager.load("fonts/Ubuntu32.fnt", BitmapFont::class.java)
        assetManager.load("img/actors.pack", TextureAtlas::class.java)
        assetManager.finishLoading()

        camera = OrthographicCamera()
        viewport = FitViewport(WIDTH, HEIGHT, camera)

        camera.translate(Vector2(WIDTH / 2f, HEIGHT / 2f))

        Gdx.input.inputProcessor = this

        Gdx.gl.glClearColor(0.6f, 0.6f, 0.8f, 1f)

        setupMineMap()

        val textureAtlas = assetManager.get("img/actors.pack", TextureAtlas::class.java)
        spriteBlock = Sprite(textureAtlas.findRegion("Block"))
        spriteBlock.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)

        spriteClearBlock = Sprite(textureAtlas.findRegion("ClearBlock"))
        spriteClearBlock.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)

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

    override fun pause() {
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun hide() {
    }

    fun update(delta: Float) {

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
                        spriteFlag.setPosition(x, y)
                        spriteFlag.draw(batch)
                    }

                    else -> {

                    }

                }
            }
            else if (value == Status.TAGGED_FLAG) {
                // draw flag
            }
            else if (value == Status.TAGGED_QUESTION) {
                // draw question mark
            }

        }
        batch.end()
    }

    override fun resume() {
    }

    override fun dispose() {
        assetManager.dispose()

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


    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val vec3 = Vector3(screenX.toFloat(), screenY.toFloat(), 0f)
        camera.unproject(vec3)
        val (coordX, coordY) = translateWorldCoordToMapCoord(vec3.x, vec3.y)

        when (button) {
            Input.Buttons.LEFT -> {

                // clicked on mine map
                if (coordX >=0 && coordY >= 0) {
                    when (mineMapStatus[coordY * mapWidth + coordX]) {
                        Status.UNSOLVED -> {
                            val mine = mineMap[coordY * mapWidth + coordX]

                            if (mine == Mine.MINE) {
                                // explode...
                                println("explode!!!")
                            }
                            else {
                                if (mine == Mine.EMPTY) {
                                    checkSurroundingBlocks(coordX, coordY)
                                }
                                else {
                                    mineMapStatus[coordY * mapWidth + coordX] = Status.CLEARED
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }

            Input.Buttons.RIGHT -> {

                // clicked on mine map
                if (coordX >=0 && coordY >= 0) {
                    println("Right button pressed at (${vec3.x}, ${vec3.y})")
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
