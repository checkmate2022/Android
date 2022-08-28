package com.example.avatwin.Decorator

import android.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class EventDecorator(var date : Collection<CalendarDay>): DayViewDecorator {

    //var date : Collection<CalendarDay> = mutableListOf()
    var dates: HashSet<CalendarDay> = HashSet(date)
    fun addDate(day : CalendarDay){
        dates.add(day)
    }
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(DotSpan(5F, Color.parseColor("#1D872A")))
    }


}