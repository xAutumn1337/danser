package me.wieku.danser.ui.screens

import me.wieku.danser.beatmap.BeatmapManager
import me.wieku.danser.graphics.drawables.triangles.Triangles
import me.wieku.danser.ui.common.widgets.ProgressBar
import me.wieku.framework.animation.Glider
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.graphics.drawables.containers.CircularContainer
import me.wieku.framework.graphics.drawables.containers.ColorContainer
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.TextSprite
import me.wieku.framework.gui.screen.Screen
import me.wieku.framework.gui.screen.ScreenCache
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import me.wieku.framework.math.color.Color
import org.joml.Vector2f
import org.koin.core.KoinComponent
import org.koin.core.inject

class LoadingScreen : Screen(), KoinComponent {

    private var progressBar: ProgressBar
    private var messages: TextSprite
    private val stack: ScreenCache by inject()
    private val mainMenu: MainMenu

    private val triangles: Triangles
    private val spawnRate = Glider(0.1f)
    private val triangleSpeed = Glider(0.1f)


    init {
        addChild(
            Triangles {
                fillMode = Scaling.Stretch
                spawnRate = 0.1f
                startOnScreen = false
                colorDark = Color(0.054f, 0.3f)
                colorLight = Color(0.2f, 0.3f)
            }.also { triangles = it },
            CircularContainer {
                fillMode = Scaling.Fit
                scale = Vector2f(0.63f)
                addChild(
                    ColorContainer {
                        fillMode = Scaling.Stretch
                        color = Color(0f, 0.6f)
                    }
                )
            },
            Sprite("menu/coin-overlay.png") {
                fillMode = Scaling.Fit
                scale = Vector2f(0.66f)
            },
            /*TextSprite("Exo2") {
                text = "Loading awesomeness"
                fontSize = 32f
                anchor = Origin.Custom
                customAnchor = Vector2f(0.5f, 0.9f)

                scaleToSize = true
                fillMode = Scaling.Fit
                scale = Vector2f(0.3f)
            },*/
            TextSprite("Exo2") {
                text = "Early build. Please visit github.com/Wieku/danser for more info"
                fillMode = Scaling.Stretch
                scaleToSize = true

                scale = Vector2f(1f, 0.02f)
                anchor = Origin.TopLeft
                origin = Origin.TopLeft
            },
            ProgressBar {
                fillMode = Scaling.Fit
                scale = Vector2f(0.8f, 0.02f)
                anchor = Origin.Custom
                //customAnchor = Vector2f(0.5f, 0.94f)
                customAnchor = Vector2f(0.5f, 0.90f)
            }. also { progressBar = it  },
            TextSprite("Exo2") {
                text = "abcdef"
                fontSize = 24f
                anchor = Origin.Custom
                customAnchor = Vector2f(0.5f, 0.93f)

                scaleToSize = true
                fillMode = Scaling.FillY
                scale = Vector2f(0.025f)
            }.also { messages = it }
        )

        mainMenu = MainMenu()
    }

    override fun update() {
        spawnRate.update(clock.currentTime)
        triangleSpeed.update(clock.currentTime)
        triangles.spawnRate = spawnRate.value
        triangles.baseVelocity = triangleSpeed.value
        super.update()
    }

    override fun onEnter(previous: Screen?) {
        super.onEnter(previous)

        triangleSpeed.addEvent(clock.currentTime, clock.currentTime + 500, 4f,0.2f, Easing.OutQuad)

        spawnRate.addEvent(clock.currentTime, clock.currentTime + 500, 0.1f,0.4f, Easing.OutQuad)

        addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime + 2000,
                0f,
                1f
            ), false
        )

        Thread {
            BeatmapManager.listener = ::onLoading
            BeatmapManager.messageListener = ::onMessage
            BeatmapManager.start()
            BeatmapManager.loadBeatmaps(System.getenv("localappdata") + "\\osu!\\Songs")
            stack.push(mainMenu)
        }.start()
    }

    override fun onSuspend(next: Screen?) {
        super.onExit(next)

        triangles.baseVelocity = 3f
        triangles.spawnEnabled = false

        triangleSpeed.addEvent(clock.currentTime + 500, 5f, Easing.OutQuad)

        addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime,
                clock.currentTime + 500,
                1f,
                0f
            ), false
        )
    }

    fun onLoading(step: Int, maxSteps: Int, message: String) {
        progressBar.progress = step.toFloat() / maxSteps
        messages.text = message
    }

    fun onMessage(message: String) {
        messages.text = message
    }

}