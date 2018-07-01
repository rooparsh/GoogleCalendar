package com.example.rooparshkalia.googlecalendardemo

import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.services.calendar.model.Event

interface GoogleCalendarPresenter {

    fun onAddEvent(event: Event)

    fun onAttach()

    fun onDetach()

    fun checkGooglePlayServices(googleApiAvailability: GoogleApiAvailability)
}