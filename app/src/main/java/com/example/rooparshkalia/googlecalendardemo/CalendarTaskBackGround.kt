package com.example.rooparshkalia.googlecalendardemo

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.HttpTransport

import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events

class CalendarTaskBackGround internal constructor(mService: Calendar,
                                                  type: Constants.CalendarTaskType,
                                                  event: Event) : AsyncTask<Event, Unit, Unit>() {


    override fun doInBackground(vararg params: Event?) {
        try {
            when (mType) {
                Constants.CalendarTaskType.INSERT -> addEventToCalendar(mEvent)
                Constants.CalendarTaskType.DELETE -> deleteEventFromCalendar("12")
                Constants.CalendarTaskType.GET -> getEventFromCalendar("12")
                Constants.CalendarTaskType.UPDATE -> updateEventToCalendar("12", mEvent)
                Constants.CalendarTaskType.GET_ALL -> getAllEventsFromCalendar()
            }
        } catch (e: Exception) {
            mLastError = e
            cancel(true)
        }
    }

    override fun onPostExecute(result: Unit?) {
        Log.d("Success", "added")
        super.onPostExecute(result)
    }

    private var mService: Calendar? = null
    private var mLastError: Exception? = null
    private var mType: Constants.CalendarTaskType? = null
    private var mEvent: Event? = null


    init {
        mType = type
        mEvent = event
    }


    override fun onCancelled() {
        if (mLastError != null) {
            if (mLastError is UserRecoverableAuthIOException) {
                val e = mLastError as UserRecoverableAuthIOException
                (mContext as Activity).startActivityForResult(e.intent, Constants.REQUEST_AUTHORISATION)
            } else {
                mLastError!!.printStackTrace()
            }
        }
    }

    private fun addEventToCalendar(event: Event?): Event? = mService?.events()?.insert(Constants.CALENDAR_ID, event)?.execute()

    private fun deleteEventFromCalendar(eventId: String) = mService?.events()?.delete(Constants.CALENDAR_ID, eventId)?.execute()

    private fun getEventFromCalendar(eventId: String): Event? = mService?.events()?.get(Constants.CALENDAR_ID, eventId)?.execute()

    private fun getAllEventsFromCalendar(): Events? = mService?.events()?.list(Constants.CALENDAR_ID)?.execute()

    private fun updateEventToCalendar(eventId: String, event: Event?) = mService?.events()?.update(Constants.CALENDAR_ID, eventId, event)?.execute()

}