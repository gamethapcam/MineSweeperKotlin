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
    SOLVED,
    TAGGED_CLEAR,
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
    private var mineNumber: Int = mapSize / 10

    private lateinit var mineMap: Array<Mine>
    private lateinit var mineMapStatus: Array<Status>

    private lateinit var blockSprite: Sprite


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

        blockSprite = Sprite(assetManager.get("img/actors.pack", TextureAtlas::class.java).findRegion("Block"))
        blockSprite.setBounds(0f, 0f, BLOCK_SIZE, BLOCK_SIZE)
    }

    fun setupMineMap() {
        mineMapStatus = Array(mapSize, {i -> Status.UNSOLVED })
        mineMap = Array(mapSize, {i -> Mine.EMPTY })

        val random = Random()
        for (i in 0..mineNumber - 1) {
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
            val y = (HEIGHT - 6f) / 2f - (mapHeight / 2f - 1 - (index / mapWidth - 1)) * BLOCK_SIZE
            blockSprite.setPosition(x, y)
            blockSprite.draw(batch)
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
        val mapCoord = translateWorldCoordToMapCoord(vec3.x, vec3.y)

        when (button) {
            Input.Buttons.LEFT -> {

                // clicked on mine map
                if (mapCoord.x >=0 && mapCoord.y >= 0) {
                    println("Left button pressed at (${mapCoord.x}, ${mapCoord.y})")
                }
            }

            Input.Buttons.RIGHT -> {

                // clicked on mine map
                if (mapCoord.x >=0 && mapCoord.y >= 0) {
                    println("Right button pressed at (${vec3.x}, ${vec3.y})")
                }
            }
        }

        return false
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
