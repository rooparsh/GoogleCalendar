package com.example.rooparshkalia.googlecalendardemo


import android.os.AsyncTask
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events

class CalendarTaskBackGround internal constructor(private val mService: Calendar,
                                                  private val mType: Constants.CalendarTaskType,
                                                  private val mEvent: Event,
                                                  private val mListener: CalendarAsyncListener) : AsyncTask<Unit, Unit, Unit>() {

    private lateinit var mLastError: Exception

    override fun doInBackground(vararg params: Unit?) {
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
        mListener.onSuccess()
        super.onPostExecute(result)
    }


    override fun onCancelled() {
        if (mLastError != null) {
            mListener.onFailure(mLastError)
        }
    }

    private fun addEventToCalendar(event: Event?): Event? = mService.events().insert(Constants.CALENDAR_ID, event).execute()

    private fun deleteEventFromCalendar(eventId: String) = mService.events().delete(Constants.CALENDAR_ID, eventId).execute()

    private fun getEventFromCalendar(eventId: String): Event? = mService.events().get(Constants.CALENDAR_ID, eventId).execute()

    private fun getAllEventsFromCalendar(): Events? = mService.events().list(Constants.CALENDAR_ID).execute()

    private fun updateEventToCalendar(eventId: String, event: Event?) = mService.events().update(Constants.CALENDAR_ID, eventId, event).execute()

}