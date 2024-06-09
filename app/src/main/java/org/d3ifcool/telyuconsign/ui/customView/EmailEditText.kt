package org.d3ifcool.telyuconsign.ui.customView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import org.d3ifcool.telyuconsign.R

class EmailEditText : AppCompatEditText  {

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

        hint = resources.getString(R.string.email)
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START

        addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                error = if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    "Email Not Valid"
                } else {
                    null
                }
            }

            override fun afterTextChanged(s: Editable) {


            }
        })
    }
}