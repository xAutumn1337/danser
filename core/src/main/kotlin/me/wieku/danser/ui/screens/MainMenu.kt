package me.wieku.danser.ui.screens

import me.wieku.danser.beatmap.Beatmap
import me.wieku.danser.beatmap.TrackManager
import me.wieku.danser.graphics.drawables.DanserCoin
import me.wieku.danser.graphics.drawables.SideFlashes
import me.wieku.danser.ui.mainmenu.RightButtonsContainer
import me.wieku.framework.animation.Transform
import me.wieku.framework.animation.TransformType
import me.wieku.framework.di.bindable.Bindable
import me.wieku.framework.di.bindable.BindableListener
import me.wieku.framework.graphics.drawables.containers.*
import me.wieku.framework.graphics.drawables.sprite.Sprite
import me.wieku.framework.graphics.drawables.sprite.TextSprite
import me.wieku.framework.gui.screen.Screen
import me.wieku.framework.input.InputManager
import me.wieku.framework.math.Easing
import me.wieku.framework.math.Origin
import me.wieku.framework.math.Scaling
import org.joml.Vector2f
import org.joml.Vector4f
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainMenu : Screen(), KoinComponent {

    private val beatmapBindable: Bindable<Beatmap?> by inject()
    private val inputManager: InputManager by inject()

    private var colorContainer: ColorContainer
    private lateinit var coin: DanserCoin
    private lateinit var left: Container
    private lateinit var buttons: RightButtonsContainer

    init {
        val bgSprite = Sprite("menu/backgrounds/background-1.png") {
            fillMode = Scaling.Fill
            anchor = Origin.Centre
        }

        addChild(
            ParallaxContainer {
                parallaxAmount = 1f/40
                fillMode = Scaling.Stretch
                addChild(
                    BlurredContainer {
                        fillMode = Scaling.Stretch
                        scale = Vector2f(1.2f)
                        blurAmount = 0.3f
                        anchor = Origin.Custom
                        customAnchor = Vector2f(0.5f, 0.5f)
                        addChild(
                            bgSprite
                        )
                    }/*.also { background = it }*/
                )
            },
            ParallaxContainer {
                parallaxAmount = 1f/80
                fillMode = Scaling.Stretch

                addChild(
                    ColorContainer {
                        color = Vector4f(0.2f, 0.2f, 0.2f, 1f)
                        scale = Vector2f(1f, 0.12f)
                        fillMode = Scaling.StretchY
                        origin = Origin.CentreRight
                        anchor = Origin.None
                    }.also { left = it },
                    RightButtonsContainer().also { buttons = it },
                    DanserCoin().apply {
                        scale = Vector2f(0.6f)
                        anchor = Origin.Custom
                        customAnchor = Vector2f(0.3f, 0.5f)
                        fillMode = Scaling.Fit
                        inheritColor = false
                    }.also { coin = it }
                )
            }
        )

        val text = TextSprite("Exo2") {
            text = "Nothing is playing"
            scaleToSize = true
            drawShadow = true
            shadowOffset = Vector2f(0.15f, 0.15f)
            fillMode = Scaling.FillY
            anchor = Origin.CentreRight
            origin = Origin.CentreRight
        }

        beatmapBindable.addListener(object : BindableListener<Beatmap?> {
            override fun valueChanged(bindable: Bindable<Beatmap?>) {
                if (bindable.value != null) {
                    text.text = String.format(
                        "%s - %s",
                        bindable.value!!.beatmapMetadata.artist,
                        bindable.value!!.beatmapMetadata.title
                    )
                }
            }
        })

        addChild(
            YogaContainer {
                isRoot = true
                origin = Origin.TopLeft
                anchor = Origin.TopLeft
                fillMode = Scaling.Stretch
                scale = Vector2f(1f, 0.04f)
                addChild(
                    ColorContainer {
                        fillMode = Scaling.Stretch
                        color = Vector4f(0.2f, 0.2f, 0.2f, 1f)
                    },
                    YogaContainer {
                        yogaSizePercent = Vector2f(100f)
                        yogaPaddingPercent = Vector4f(20f)
                        addChild(
                            YogaContainer {
                                yogaSizePercent = Vector2f(100f)
                                addChild(
                                    text
                                )
                            }
                        )
                    }
                )
            },

            SideFlashes().apply {
                fillMode = Scaling.Stretch
            },
            ColorContainer {
                fillMode = Scaling.Stretch
                drawForever = false
                color.w = 0f
            }.also { colorContainer = it }
        )
        super.color.w = 0f
    }

    override fun update() {
        TrackManager.update()

        super.update()

        if (coin.wasUpdated) {
            left.position.set(coin.drawSize).mul(0.10f, 0.5f).add(coin.drawPosition)
            left.size.x = buttons.position.x
            left.invalidate()
            left.update()

            buttons.position.set(coin.drawSize).mul(0.9f, 0.5f).add(coin.drawPosition)
            buttons.size.x = drawSize.x - buttons.position.x
            buttons.invalidate()
            buttons.update()
        }
    }

    override fun onEnter(previous: Screen?) {
        super.onEnter(previous)

        TrackManager.start()

        coin.addTransform(
            Transform(
                TransformType.Color4,
                clock.currentTime,
                clock.currentTime + 1300,
                Vector4f(0f),
                Vector4f(1f)
            )
        )
        coin.addTransform(
            Transform(
                TransformType.Scale,
                clock.currentTime,
                clock.currentTime + 1000,
                2.5f,
                1.5f,
                Easing.Linear
            )
        )
        coin.addTransform(
            Transform(
                TransformType.Scale,
                clock.currentTime + 1000,
                clock.currentTime + 1300,
                1.5f,
                0.35f,
                Easing.OutBack
            )
        )

        colorContainer.addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime + 1300,
                clock.currentTime + 1800,
                1f,
                0f
            )
        )

        addTransform(
            Transform(
                TransformType.Fade,
                clock.currentTime + 1000,
                clock.currentTime + 1300,
                0.5f,
                1f
            )
        )
    }


}