package com.example.rooparshkalia.googlecalendardemo

import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event

class GoogleCalendarPresenterImpl constructor(private val mView: GoogleCalendarView,
                                              private val mGoogleCalendarInteractorImpl: GoogleCalendarInteractorImpl) : GoogleCalendarPresenter {


    override fun updateCalendar(service: Calendar, credential: GoogleAccountCredential, type: Constants.CalendarTaskType) {
        when (type) {
            Constants.CalendarTaskType.INSERT -> {
                mGoogleCalendarInteractorImpl.apiCallForAddingEvent(service, events = Event())
            }
            Constants.CalendarTaskType.UPDATE -> {
            }
            Constants.CalendarTaskType.DELETE -> {
            }
            Constants.CalendarTaskType.GET -> {
            }
            Constants.CalendarTaskType.GET_ALL -> {
            }
        }
    }

    override fun checkGooglePlayServices(googleApiAvailability: GoogleApiAvailability, type: Constants.CalendarTaskType) {
        if (!isGoogleServicesAvailable(googleApiAvailability)) {
            val connectionResult = acquirePlayServices(googleApiAvailability)
            if (googleApiAvailability.isUserResolvableError(connectionResult)) {
                mView.onGoogleServicesNotAvailable(googleApiAvailability, connectionResult)
            }
        } else {
            mView.requestUserAccount(type)
        }
    }

    private fun acquirePlayServices(googleApiAvailability: GoogleApiAvailability): Int =
            mView.acquireGooglePlayServices(googleApiAvailability)

    private fun isGoogleServicesAvailable(googleApiAvailability: GoogleApiAvailability): Boolean =
            mView.checkGoogleServicesAvailability(googleApiAvailability)


    var mIsViewAttached: Boolean = false

    override fun onAttach() {
        mIsViewAttached = true
    }

    override fun onDetach() {
        mIsViewAttached = false
    }


    override fun onAddEvent(event: Event) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}