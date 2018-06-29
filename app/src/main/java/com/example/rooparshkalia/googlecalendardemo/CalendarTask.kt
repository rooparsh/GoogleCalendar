package com.example.rooparshkalia.googlecalendardemo

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import java.lang.ref.WeakReference
import java.util.*

class CalendarTask internal constructor(context: Context, credential: GoogleAccountCredential) : AsyncTask<Event, Unit, Unit>() {

    override fun doInBackground(vararg params: Event?) {
        try {
            addEventToCalendar()
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
    private var mContext: WeakReference<Context>? = null

    companion object {
        private const val CALENDAR_ID = "primary"
        private const val REQUEST_AUTHORISATION = 1002
    }

    init {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        mContext = WeakReference(context)
        mService = Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("Google Calendar Demo")
                .build()
    }

    private fun addEventToCalendar(): Event {

        var event = Event().setSummary("Test Event")
                .setLocation("Ludhiana")
                .setDescription("First Event in Ludhiana")

        val startDateTime = DateTime("2018-07-02T09:00:00Z")
        val timeZone = java.util.Calendar.getInstance().timeZone.getDisplayName(false, TimeZone.SHORT)
        val eventStartDateTime = EventDateTime().setDateTime(startDateTime).setTimeZone(timeZone)

        val endDateTime = DateTime("2018-07-02T10:00:00Z")
        val eventEndDateTime = EventDateTime().setDateTime(endDateTime).setTimeZone(timeZone)

        event.start = eventStartDateTime
        event.end = eventEndDateTime

        event = mService?.events()?.insert(CALENDAR_ID, event)?.execute()
        return event
    }

    override fun onCancelled() {
        if (mLastError != null) {
            if (mLastError is UserRecoverableAuthIOException) {
                val e = mLastError as UserRecoverableAuthIOException
                ActivityCompat.startActivityForResult(mContext as Activity, e.intent, REQUEST_AUTHORISATION, null)
            } else {
                mLastError!!.printStackTrace()
            }
        }
    }

}