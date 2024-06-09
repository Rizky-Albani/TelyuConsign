package org.d3ifcool.telyuconsign.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import org.d3ifcool.telyuconsign.R

class CustomButton : AppCompatButton  {

    private var txtColor : Int = 0
    private lateinit var enabledBg : Drawable
    private lateinit var disabledBg : Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {

        txtColor = ContextCompat.getColor(context, android.R.color.white)
        enabledBg = ContextCompat.getDrawable(context, R.drawable.button_enabled) as Drawable
        disabledBg = ContextCompat.getDrawable(context, R.drawable.button_disabled) as Drawable
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        background = if (isEnabled) enabledBg else disabledBg

        setTextColor(txtColor)
        textSize = 12f
        gravity = Gravity.CENTER

    }

}