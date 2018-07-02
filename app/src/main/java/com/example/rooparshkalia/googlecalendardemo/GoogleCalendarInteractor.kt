package com.example.rooparshkalia.googlecalendardemo

import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event

interface GoogleCalendarInteractor {
    fun apiCallForAddingEvent(service: Calendar, event: Event, listener: CallbackListener)


    interface CallbackListener {
        fun onSuccess()

        fun onFailure(mLastError: Exception)
    }
}