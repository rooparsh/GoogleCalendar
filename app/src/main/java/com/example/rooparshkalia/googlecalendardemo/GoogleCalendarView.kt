package com.example.rooparshkalia.googlecalendardemo

import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.services.calendar.model.Event

interface GoogleCalendarView {

    fun onSuccess(events: List<Event>)

    fun onGoogleServicesNotAvailable(googleApiAvailability: GoogleApiAvailability, connectionResult: Int)

    fun checkGoogleServicesAvailability(googleApiAvailability: GoogleApiAvailability): Boolean

    fun acquireGooglePlayServices(googleApiAvailability: GoogleApiAvailability): Int

    fun requestUserAccount()
}