package com.example.rooparshkalia.googlecalendardemo

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.rooparshkalia.googlecalendardemo.Constants.REQUEST_AUTHORISATION
import com.example.rooparshkalia.googlecalendardemo.Constants.TYPE
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.DateTime
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

abstract class GoogleCalendarActivity : Activity(), EasyPermissions.PermissionCallbacks, View.OnClickListener {


    private lateinit var mCredential: GoogleAccountCredential
    private lateinit var mEvent: Event
    private lateinit var mEventId: String
    private lateinit var mType: Constants.CalendarTaskType

    companion object {
        private val CALENDAR_SCOPES = arrayListOf(CalendarScopes.CALENDAR)
        private const val REQUEST_PERMISSION_ACCOUNT = 1000
        private const val REQUEST_ACCOUNT_PICKER_EVENT = 1001
        private const val REQUEST_WRITE_CALENDAR = 1002
        private const val REQUEST_GOOGLE_PLAY_SERVICES = 1003
        private const val PREF_ACCOUNT_NAME = "accountName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCredential = GoogleAccountCredential.usingOAuth2(this, CALENDAR_SCOPES).setBackOff(ExponentialBackOff())
    }

    fun addEvent(event: Event) {
        mEvent = event
        checkGooglePlayServices(Constants.CalendarTaskType.INSERT, GoogleApiAvailability.getInstance())
    }


    fun updateEvent(eventId: String, event: Event) {
        mEvent = event
        mEventId = eventId
        checkGooglePlayServices(Constants.CalendarTaskType.UPDATE, GoogleApiAvailability.getInstance())
    }


    fun getEvent(eventId: String) {
        mEventId = eventId
        checkGooglePlayServices(Constants.CalendarTaskType.GET, GoogleApiAvailability.getInstance())
    }


    fun getAllEvents() =
            checkGooglePlayServices(Constants.CalendarTaskType.GET_ALL, GoogleApiAvailability.getInstance())


    fun deleteEvent(eventId: String) {
        mEventId = eventId
        checkGooglePlayServices(Constants.CalendarTaskType.DELETE, GoogleApiAvailability.getInstance())
    }


    private fun checkGooglePlayServices(type: Constants.CalendarTaskType, googleApiAvailability: GoogleApiAvailability) {
        if (!isGooglePlayServicesAvailable(googleApiAvailability)) {
            acquirePlayServices(googleApiAvailability)
        } else {
            requestUserAccount(type)
        }
    }

    private fun acquirePlayServices(googleApiAvailability: GoogleApiAvailability) {
        val connectionStatusResult = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (googleApiAvailability.isUserResolvableError(connectionStatusResult)) {
            showPlayServicesAvailabilityErrorDialog(googleApiAvailability, connectionStatusResult)
        }
    }

    private fun showPlayServicesAvailabilityErrorDialog(googleApiAvailability: GoogleApiAvailability, connectionStatusResult: Int) =
            googleApiAvailability.getErrorDialog(this, connectionStatusResult, REQUEST_GOOGLE_PLAY_SERVICES).show()

    private fun isGooglePlayServicesAvailable(googleApiAvailability: GoogleApiAvailability): Boolean =
            googleApiAvailability.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS

    @AfterPermissionGranted(REQUEST_PERMISSION_ACCOUNT)
    private fun requestUserAccount(type: Constants.CalendarTaskType) =
            if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
                val accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null)
                accountName?.let {
                    mCredential.selectedAccountName = it
                    requestCalendar(type)
                }
                        ?: startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER_EVENT)
            } else {
                EasyPermissions.requestPermissions(
                        this,
                        "This app needs to access your Google account",
                        REQUEST_PERMISSION_ACCOUNT,
                        Manifest.permission.GET_ACCOUNTS)
            }

    private fun saveUserCredentials(data: Intent) {
        val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
        accountName?.let {
            val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(PREF_ACCOUNT_NAME, it)
            editor.apply()
            mCredential.selectedAccountName = it
            requestCalendar(mType)
        }
    }

    @AfterPermissionGranted(REQUEST_WRITE_CALENDAR)
    private fun requestCalendar(type: Constants.CalendarTaskType) =
            if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_CALENDAR)) {
                updateCalendar(type)
            } else {
                EasyPermissions.requestPermissions(
                        this,
                        "This app needs to write to your calendar",
                        REQUEST_PERMISSION_ACCOUNT,
                        Manifest.permission.WRITE_CALENDAR)
            }

    private fun updateCalendar(type: Constants.CalendarTaskType) {
        when (type) {
            Constants.CalendarTaskType.INSERT -> CalendarTask(this, Constants.CalendarTaskType.INSERT, mCredential).setEvent(mEvent).execute()
            Constants.CalendarTaskType.DELETE -> CalendarTask(this, Constants.CalendarTaskType.DELETE, mCredential).setEventId(mEventId).execute()
            Constants.CalendarTaskType.UPDATE -> CalendarTask(this, Constants.CalendarTaskType.UPDATE, mCredential).setEventId(mEventId).setEvent(mEvent).execute()
            Constants.CalendarTaskType.GET -> CalendarTask(this, Constants.CalendarTaskType.GET, mCredential).setEventId(mEventId).execute()
            Constants.CalendarTaskType.GET_ALL -> CalendarTask(this, Constants.CalendarTaskType.GET_ALL, mCredential).execute()
        }
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
        Log.e("Easy Permission", "Permission not Granted")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
        Log.e("Easy Permission", "Permission Granted")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_ACCOUNT_PICKER_EVENT -> data?.let {
                    data.extras?.let { saveUserCredentials(data) }
                }
                REQUEST_AUTHORISATION -> data?.let { CalendarTask(this, Constants.CalendarTaskType.valueOf(data.extras.getString(TYPE)), mCredential).execute() }
            }
        }
    }
}