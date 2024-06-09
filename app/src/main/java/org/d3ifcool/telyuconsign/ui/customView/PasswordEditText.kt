package org.d3ifcool.telyuconsign.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import org.d3ifcool.telyuconsign.R

class PasswordEditText : AppCompatEditText {

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

        addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                error = if (s.length < 8) {
                    context.getString(R.string.password_min)
                } else {
                    null
                }
            }

            override fun afterTextChanged(s: Editable) {


            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        hint = resources.getString(R.string.password)
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
    }
}