package com.example.rooparshkalia.googlecalendardemo

import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event


class GoogleCalendarInteractorImpl : GoogleCalendarInteractor {
    override fun apiCallForAddingEvent(service: Calendar, event: Event) {
        CalendarTaskBackGround(service, Constants.CalendarTaskType.INSERT, event).execute()
    }

}