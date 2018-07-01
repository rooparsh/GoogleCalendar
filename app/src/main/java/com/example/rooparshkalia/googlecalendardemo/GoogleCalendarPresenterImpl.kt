package com.example.rooparshkalia.googlecalendardemo

import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.services.calendar.model.Event

class GoogleCalendarPresenterImpl(val mView: GoogleCalendarView) : GoogleCalendarPresenter {

    override fun checkGooglePlayServices(googleApiAvailability: GoogleApiAvailability) {
        if (!isGoogleServicesAvailable(googleApiAvailability)) {
            val connectionResult = acquirePlayServices(googleApiAvailability)
            if (googleApiAvailability.isUserResolvableError(connectionResult)) {
                mView.onGoogleServicesNotAvailable(googleApiAvailability, connectionResult)
            }
        } else {
            mView.requestUserAccount()
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