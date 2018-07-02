package com.example.rooparshkalia.googlecalendardemo

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.example.rooparshkalia.googlecalendardemo.Constants.CALENDAR_ID
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import java.lang.ref.WeakReference
import com.example.rooparshkalia.googlecalendardemo.Constants.CalendarTaskType.*
import com.example.rooparshkalia.googlecalendardemo.Constants.REQUEST_AUTHORISATION
import com.example.rooparshkalia.googlecalendardemo.Constants.TYPE

class CalendarTask internal constructor(activity: Activity,
                                        type: Constants.CalendarTaskType,
                                        credential: GoogleAccountCredential) : AsyncTask<Unit, Unit, Unit>() {


    override fun doInBackground(vararg params: Unit?) {
        try {
            when (mType) {
                INSERT -> addEventToCalendar(mEvent)
                DELETE -> deleteEventFromCalendar(mEventId)
                GET -> getEventFromCalendar(mEventId)
                UPDATE -> updateEventToCalendar(mEventId, mEvent)
                GET_ALL -> getAllEventsFromCalendar()
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
    private var mActivity: WeakReference<Activity>? = null
    private var mType: Constants.CalendarTaskType
    private lateinit var mEvent: Event
    private lateinit var mEventId: String


    init {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        mActivity = WeakReference(activity)
        mType = type
        mService = Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName(mActivity?.get()?.getString(R.string.app_name))
                .build()
    }


    override fun onCancelled() {
        if (mLastError != null) {
            if (mLastError is UserRecoverableAuthIOException) {
                val bundle = Bundle()
                bundle.putString(TYPE, mType.name)
                val e = mLastError as UserRecoverableAuthIOException
                mActivity?.get()?.startActivityForResult(e.intent, REQUEST_AUTHORISATION, bundle)
            } else {
                mLastError!!.printStackTrace()
            }
        }
    }

    fun setEvent(event: Event): CalendarTask {
        this.mEvent = event
        return this
    }

    fun setEventId(eventId: String): CalendarTask {
        this.mEventId = eventId
        return this
    }

    private fun addEventToCalendar(event: Event): Event? = mService?.events()?.insert(CALENDAR_ID, event)?.execute()

    private fun deleteEventFromCalendar(eventId: String) = mService?.events()?.delete(CALENDAR_ID, eventId)?.execute()

    private fun getEventFromCalendar(eventId: String): Event? = mService?.events()?.get(CALENDAR_ID, eventId)?.execute()

    private fun getAllEventsFromCalendar(): Events? = mService?.events()?.list(CALENDAR_ID)?.execute()

    private fun updateEventToCalendar(eventId: String, event: Event) = mService?.events()?.update(CALENDAR_ID, eventId, event)?.execute()

}

