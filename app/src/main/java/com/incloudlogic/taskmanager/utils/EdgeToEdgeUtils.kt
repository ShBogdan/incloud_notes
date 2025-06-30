package com.incloudlogic.taskmanager.utils

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Утилитный класс для настройки edge-to-edge интерфейса на Android.
 *
 * Предоставляет методы для корректного отображения контента под системными панелями
 * (status bar и navigation bar) с учётом отступов (insets).
 *
 * Использование edge-to-edge повышает визуальное качество и соответствует Material Design рекомендациям.
 */
object EdgeToEdgeUtils {

    /**
     * Применяет системные отступы (WindowInsets) к указанному корневому View,
     * позволяя безопасно отрисовывать контент под статус-баром и/или навигационной панелью.
     *
     * Этот метод должен вызываться после {@code setContentView(...)} и {@code enableEdgeToEdge()}.
     *
     * @param rootViewId ID корневого View (например, RelativeLayout или ConstraintLayout),
     *                   к которому будут применены отступы системных панелей.
     * @param activity Активити, внутри которой вызывается метод.
     */
    fun applyEdgeToEdgePadding(@IdRes rootViewId: Int, activity: Activity) {
        val root = activity.findViewById<View>(rootViewId)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}