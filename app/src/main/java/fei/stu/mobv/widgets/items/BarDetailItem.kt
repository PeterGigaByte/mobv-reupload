package fei.stu.mobv.widgets.items

import fei.stu.mobv.enums.BarType

class BarDetailItem(val key: String, val value: String) {
    fun getBarType(): BarType {
        return try {
            BarType.valueOf(value.uppercase())
        } catch (e: IllegalArgumentException) {
            BarType.OTHER
        }
    }
}