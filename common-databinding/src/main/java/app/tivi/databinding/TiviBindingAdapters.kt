/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.tivi.databinding

import android.content.res.Resources
import android.graphics.Outline
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import app.tivi.extensions.doOnApplyWindowInsets
import app.tivi.extensions.resolveThemeReferenceResId
import app.tivi.ui.MaxLinesToggleClickListener
import app.tivi.ui.ScrimUtil
import kotlin.math.roundToInt

@BindingAdapter("visibleIfNotNull")
fun visibleIfNotNull(view: View, target: Any?) {
    view.isVisible = target != null
}

@BindingAdapter("visible")
fun visible(view: View, value: Boolean) {
    view.isVisible = value
}

@BindingAdapter("textOrGoneIfEmpty")
fun textOrGoneIfEmpty(view: TextView, s: CharSequence?) {
    view.text = s
    view.isGone = s.isNullOrEmpty()
}

@BindingAdapter("srcRes")
fun imageViewSrcRes(view: ImageView, drawableRes: Int) {
    if (drawableRes != 0) {
        view.setImageResource(drawableRes)
    } else {
        view.setImageDrawable(null)
    }
}

@BindingAdapter("maxLinesToggle")
fun maxLinesClickListener(view: TextView, oldCollapsedMaxLines: Int, newCollapsedMaxLines: Int) {
    if (oldCollapsedMaxLines != newCollapsedMaxLines) {
        // Default to collapsed
        view.maxLines = newCollapsedMaxLines
        // Now set click listener
        view.setOnClickListener(MaxLinesToggleClickListener(newCollapsedMaxLines))
    }
}

@BindingAdapter("backgroundScrim")
fun backgroundScrim(view: View, oldColor: Int, color: Int) {
    if (oldColor != color) {
        view.background = ScrimUtil.makeCubicGradientScrimDrawable(color, 16, Gravity.BOTTOM)
    }
}

@BindingAdapter("foregroundScrim")
fun foregroundScrim(view: View, oldColor: Int, color: Int) {
    if (oldColor != color) {
        view.foreground = ScrimUtil.makeCubicGradientScrimDrawable(color, 16, Gravity.BOTTOM)
    }
}

@BindingAdapter("topCornerOutlineProvider")
fun topCornerOutlineProvider(view: View, oldRadius: Float, radius: Float) {
    view.clipToOutline = true
    if (oldRadius != radius) {
        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height + radius.roundToInt(), radius)
            }
        }
    }
}

@BindingAdapter("roundedCornerOutlineProvider")
fun roundedCornerOutlineProvider(view: View, oldRadius: Float, radius: Float) {
    view.clipToOutline = true
    if (oldRadius != radius) {
        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
    }
}

@BindingAdapter("textAppearanceAttr")
fun textAppearanceAttr(view: TextView, oldTextAppearanceStyleAttr: Int, textAppearanceStyleAttr: Int) {
    if (oldTextAppearanceStyleAttr != textAppearanceStyleAttr) {
        view.setTextAppearance(view.context.resolveThemeReferenceResId(textAppearanceStyleAttr))
    }
}

@BindingAdapter("fontFamily")
fun fontFamily(view: TextView, oldFontFamily: Int, fontFamily: Int) {
    if (oldFontFamily != fontFamily) {
        view.typeface = try {
            ResourcesCompat.getFont(view.context, fontFamily)
        } catch (nfe: Resources.NotFoundException) {
            null
        } ?: Typeface.DEFAULT
    }
}

@BindingAdapter(
        "paddingLeftSystemWindowInsets",
        "paddingTopSystemWindowInsets",
        "paddingRightSystemWindowInsets",
        "paddingBottomSystemWindowInsets",
        "paddingLeftGestureInsets",
        "paddingTopGestureInsets",
        "paddingRightGestureInsets",
        "paddingBottomGestureInsets",
        requireAll = false
)
fun applySystemWindows(
    view: View,
    systemWindowLeft: Boolean,
    systemWindowTop: Boolean,
    systemWindowRight: Boolean,
    systemWindowBottom: Boolean,
    gestureInsetsLeft: Boolean,
    gestureInsetsTop: Boolean,
    gestureInsetsRight: Boolean,
    gestureInsetsBottom: Boolean
) {
    require(!((systemWindowLeft && gestureInsetsLeft) ||
            (systemWindowTop && gestureInsetsTop) ||
            (systemWindowRight && gestureInsetsRight) ||
            (systemWindowBottom && gestureInsetsBottom))) {
        "Invalid parameters. Can not request system window and gesture inset handling for the same dimension"
    }

    view.doOnApplyWindowInsets { v, insets, paddingState ->
        val left = when {
            gestureInsetsLeft -> insets.systemGestureInsets.left
            systemWindowLeft -> insets.systemWindowInsetLeft
            else -> 0
        }
        val top = when {
            gestureInsetsTop -> insets.systemGestureInsets.top
            systemWindowTop -> insets.systemWindowInsetTop
            else -> 0
        }
        val right = when {
            gestureInsetsRight -> insets.systemGestureInsets.right
            systemWindowRight -> insets.systemWindowInsetRight
            else -> 0
        }
        val bottom = when {
            gestureInsetsBottom -> insets.systemGestureInsets.bottom
            systemWindowBottom -> insets.systemWindowInsetBottom
            else -> 0
        }
        v.setPadding(
                paddingState.left + left,
                paddingState.top + top,
                paddingState.right + right,
                paddingState.bottom + bottom
        )
    }
}