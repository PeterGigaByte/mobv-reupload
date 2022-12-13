package fei.stu.mobv.widgets.events

import fei.stu.mobv.database.items.BarItem

interface BarsEvents {
    fun onBarClick(bar: BarItem)
}