package com.example.rooparshkalia.googlecalendardemo

import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event

class GoogleCalendarPresenterImpl constructor(private val mView: GoogleCalendarView,
                                              private val mGoogleCalendarInteractorImpl: GoogleCalendarInteractorImpl) : GoogleCalendarPresenter {


    override fun updateCalendar(service: Calendar, credential: GoogleAccountCredential, type: Constants.CalendarTaskType) {
        when (type) {
            Constants.CalendarTaskType.INSERT -> mGoogleCalendarInteractorImpl.apiCallForAddingEvent(service, Event(), object : GoogleCalendarInteractor.CallbackListener {
                override fun onSuccess() {
                    mView.onSuccess(listOf(Event()))
                }

                override fun onFailure(mLastError: Exception) {
                    mView.requestAuthorization(mLastError, type)
                }
            })

            Constants.CalendarTaskType.UPDATE -> mGoogleCalendarInteractorImpl.apiCallForAddingEvent(service, Event(), object : GoogleCalendarInteractor.CallbackListener {
                override fun onSuccess() {
                    mView.onSuccess(listOf(Event()))

                }

                override fun onFailure(mLastError: Exception) {
                    mView.requestAuthorization(mLastError, type)
                }
            })

            Constants.CalendarTaskType.DELETE -> mGoogleCalendarInteractorImpl.apiCallForAddingEvent(service, Event(), object : GoogleCalendarInteractor.CallbackListener {
                override fun onSuccess() {
                    mView.onSuccess(listOf(Event()))

                }

                override fun onFailure(mLastError: Exception) {
                    mView.requestAuthorization(mLastError, type)
                }
            })

            Constants.CalendarTaskType.GET -> mGoogleCalendarInteractorImpl.apiCallForAddingEvent(service, Event(), object : GoogleCalendarInteractor.CallbackListener {
                override fun onSuccess() {
                    mView.onSuccess(listOf(Event()))
                }

                override fun onFailure(mLastError: Exception) {
                    mView.requestAuthorization(mLastError, type)
                }
            })

            Constants.CalendarTaskType.GET_ALL -> mGoogleCalendarInteractorImpl.apiCallForAddingEvent(service, Event(), object : GoogleCalendarInteractor.CallbackListener {
                override fun onSuccess() {
                    mView.onSuccess(listOf(Event()))
                }

                override fun onFailure(mLastError: Exception) {
                    mView.requestAuthorization(mLastError, type)
                }
            })

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

}