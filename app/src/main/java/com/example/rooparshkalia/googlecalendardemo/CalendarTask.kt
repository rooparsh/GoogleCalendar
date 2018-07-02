package com.example.rooparshkalia.googlecalendardemo

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
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

//class CalendarTask internal constructor(context: Context,
//                                        type: Constants.CalendarTaskType,
//                                        credential: GoogleAccountCredential,
//                                        event: Event) : AsyncTask<Event, Unit, Unit>() {
//
//
//    override fun doInBackground(vararg params: Event?) {
//        try {
//            when (mType) {
//                INSERT -> addEventToCalendar(mEvent)
//                DELETE -> deleteEventFromCalendar("12")
//                GET -> getEventFromCalendar("12")
//                UPDATE -> updateEventToCalendar("12", mEvent)
//                GET_ALL -> getAllEventsFromCalendar()
//            }
//        } catch (e: Exception) {
//            mLastError = e
//            cancel(true)
//        }
//    }
//
//    override fun onPostExecute(result: Unit?) {
//        Log.d("Success", "added")
//        super.onPostExecute(result)
//    }
//
//    private var mService: Calendar? = null
//    private var mLastError: Exception? = null
//    private var mContext: WeakReference<Context>? = null
//    private var mType: Constants.CalendarTaskType? = null
//    private var mEvent: Event? = null
//
//
//    init {
//        val transport = AndroidHttp.newCompatibleTransport()
//        val jsonFactory = JacksonFactory.getDefaultInstance()
//        mContext = WeakReference(context)
//        mType = type
//        mEvent = event
//        mService = Calendar.Builder(transport, jsonFactory, credential)
//                .setApplicationName("Google Calendar Demo")
//                .build()
//    }
//
//
//    override fun onCancelled() {
//        if (mLastError != null) {
//            if (mLastError is UserRecoverableAuthIOException) {
//                val e = mLastError as UserRecoverableAuthIOException
//                (mContext as Activity).startActivityForResult(e.intent, REQUEST_AUTHORISATION)
//            } else {
//                mLastError!!.printStackTrace()
//            }
//        }
//    }
//
//    private fun addEventToCalendar(event: Event?): Event? = mService?.events()?.insert(CALENDAR_ID, event)?.execute()
//
//    private fun deleteEventFromCalendar(eventId: String) = mService?.events()?.delete(CALENDAR_ID, eventId)?.execute()
//
//    private fun getEventFromCalendar(eventId: String): Event? = mService?.events()?.get(CALENDAR_ID, eventId)?.execute()
//
//    private fun getAllEventsFromCalendar(): Events? = mService?.events()?.list(CALENDAR_ID)?.execute()
//
//    private fun updateEventToCalendar(eventId: String, event: Event?) = mService?.events()?.update(CALENDAR_ID, eventId, event)?.execute()
//
//}

