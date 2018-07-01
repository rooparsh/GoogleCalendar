package com.example.rooparshkalia.googlecalendardemo

import com.google.api.services.calendar.model.Event

interface GoogleCalendarInteractor {
    fun apiCallForAddingEvent(events: Event)
}