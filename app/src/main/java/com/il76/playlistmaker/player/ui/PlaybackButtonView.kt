package com.il76.playlistmaker.player.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.il76.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var iconPlaying: Bitmap? = null
    private var iconPaused: Bitmap? = null

    // Состояние кнопки
    private var playerStatus: PlayerStatus = PlayerStatus.Default
        set(value) {
            field = value
            updateBitmap()
            invalidate() // Пересчитываем изображение
        }

    init {
        // Получение атрибутов
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlaybackButton)
        try {
            // Загрузка иконок из ресурсов
            iconPlaying = loadBitmapFromAttribute(typedArray, R.styleable.PlaybackButton_iconPlaying)
            iconPaused = loadBitmapFromAttribute(typedArray, R.styleable.PlaybackButton_iconPaused)
        } finally {
            typedArray.recycle()
        }
        // Установка начального состояния
        playerStatus = PlayerStatus.Paused
    }


    // Переключатель состояния
    fun setStatus(status: PlayerStatus) {
        playerStatus = status
    }

    // Рендеринг кнопки
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        currentBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, null, rectF, null)
        }
    }

    // Обновление битмапа в зависимости от состояния
    private fun updateBitmap() {
        currentBitmap = when (playerStatus) {
            is PlayerStatus.Playing -> iconPaused
            else -> iconPlaying
        }
    }

    // Вспомогательная функция для загрузки иконок из атрибута
    private fun loadBitmapFromAttribute(typedArray: TypedArray, attributeId: Int): Bitmap? {
        return (ResourcesCompat.getDrawable(this.resources, typedArray.getResourceId(attributeId, 0), null) as VectorDrawable).toBitmap()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_UP -> {
                // Переключаем состояние кнопки при отпускании пальца
                toggleStatus()
            }
        }
        return super.onTouchEvent(event)
    }

    // переключение состояния кнопки
    fun toggleStatus() {
        playerStatus = if (playerStatus is PlayerStatus.Playing) {
            PlayerStatus.Paused
        } else {
            PlayerStatus.Playing(0)
        }
        setStatus(playerStatus)
    }

    private lateinit var rectF: RectF

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Определяем размеры области для отрисовки
        val size = Math.min(width, height).toFloat()
        rectF = RectF((width - size) / 2, (height - size) / 2, (width + size) / 2, (height + size) / 2)
    }

    // Хранение текущего битмапа
    private var currentBitmap: Bitmap? = null


}