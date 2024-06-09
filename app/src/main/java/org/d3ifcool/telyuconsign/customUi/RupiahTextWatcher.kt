package org.d3ifcool.telyuconsign.customUi
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat
import java.text.ParseException

class RupiahTextWatcher(private val editText: EditText) : TextWatcher {

    private val decimalFormat = DecimalFormat("#,###")
    private var current = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Not used in this case
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Not used in this case
    }

    override fun afterTextChanged(editable: Editable?) {
        if (editable.toString() != current) {
            editText.removeTextChangedListener(this)

            try {
                val originalString = editable.toString()

                // Remove non-digits
                val longValue = originalString.replace("[^\\d]".toRegex(), "").toLong()

                // Format the long value to currency format
                val formattedString = decimalFormat.format(longValue)

                current = formattedString
                editText.setText(formattedString)
                editText.setSelection(formattedString.length)
            } catch (nfe: NumberFormatException) {
                nfe.printStackTrace()
            } catch (pe: ParseException) {
                pe.printStackTrace()
            }

            editText.addTextChangedListener(this)
        }
    }
}
