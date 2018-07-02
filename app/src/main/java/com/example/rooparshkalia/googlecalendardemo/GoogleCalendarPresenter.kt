package com.example.rooparshkalia.googlecalendardemo

import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event

interface GoogleCalendarPresenter {

    fun onAddEvent(event: Event)

    fun onAttach()

    fun onDetach()

    fun checkGooglePlayServices(googleApiAvailability: GoogleApiAvailability, type: Constants.CalendarTaskType)

    fun updateCalendar(service: Calendar, credential: GoogleAccountCredential, type: Constants.CalendarTaskType)
}