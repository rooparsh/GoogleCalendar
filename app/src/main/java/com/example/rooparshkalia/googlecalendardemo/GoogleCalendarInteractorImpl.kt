package com.example.rooparshkalia.googlecalendardemo


import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event


class GoogleCalendarInteractorImpl : GoogleCalendarInteractor {

    override fun apiCallForAddingEvent(service: Calendar, event: Event, listener: GoogleCalendarInteractor.CallbackListener) {
        CalendarTaskBackGround(service, Constants.CalendarTaskType.INSERT, event, object : CalendarAsyncListener {

            override fun onSuccess() {
                listener.onSuccess()
            }

            override fun onFailure(mLastError: Exception) {
                listener.onFailure(mLastError)
            }

        }).execute()
    }


}