package org.d3ifcool.telyuconsign.ui.customView

import java.text.NumberFormat
import java.util.Locale

class Converter {
    companion object {
        fun String.toCurrencyFormat(): String {
            val localeID = Locale("in", "ID")
            val doubleValue = this.toDoubleOrNull() ?: return this
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            numberFormat.minimumFractionDigits = 0
            return numberFormat.format(doubleValue)
        }
    }
}