package com.example.rooparshkalia.googlecalendardemo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

abstract class GoogleCalendarActivity : Activity(), EasyPermissions.PermissionCallbacks, GoogleCalendarView {


    override fun onSuccess(events: List<Event>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGoogleServicesNotAvailable(googleApiAvailability: GoogleApiAvailability, connectionResult: Int) {
        googleApiAvailability.getErrorDialog(this, connectionResult, REQUEST_GOOGLE_PLAY_SERVICES).show()
    }

    override fun checkGoogleServicesAvailability(googleApiAvailability: GoogleApiAvailability): Boolean =
            googleApiAvailability.isGooglePlayServicesAvailable(this).equals(ConnectionResult.SUCCESS)

    override fun acquireGooglePlayServices(googleApiAvailability: GoogleApiAvailability): Int {
        return googleApiAvailability.isGooglePlayServicesAvailable(this)
    }

    private lateinit var mCredential: GoogleAccountCredential
    private lateinit var mEvent: Event
    private lateinit var mEventid: String
    private lateinit var mGoogleCalendarPresenter: GoogleCalendarPresenter

    companion object {
        private val CALENDAR_SCOPES = arrayListOf(CalendarScopes.CALENDAR)
        private const val REQUEST_PERMISSION_ACCOUNT = 1000
        private const val REQUEST_ACCOUNT_PICKER_EVENT = 1001
        private const val REQUEST_WRITE_CALENDAR = 1003
        private const val REQUEST_GOOGLE_PLAY_SERVICES = 1004
        private const val PREF_ACCOUNT_NAME = "accountName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGoogleCalendarPresenter = GoogleCalendarPresenterImpl(this)

        mCredential = GoogleAccountCredential.usingOAuth2(this, CALENDAR_SCOPES)
                .setBackOff(ExponentialBackOff())

        mGoogleCalendarPresenter.onAttach()
    }

    override fun onDestroy() {
        mGoogleCalendarPresenter.onDetach()
        super.onDestroy()
    }

    private fun onGoogleCalendarClicked() {
        mGoogleCalendarPresenter.checkGooglePlayServices(GoogleApiAvailability.getInstance())
    }

    fun addEvent(event: Event) {
        onGoogleCalendarClicked()
        this.mEvent = event
    }

    fun updateEvent(event: Event) {
        onGoogleCalendarClicked()
        this.mEventid = event.id
        this.mEvent = event
    }


    @AfterPermissionGranted(REQUEST_PERMISSION_ACCOUNT)
    override fun requestUserAccount() =
            if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
                val accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null)

                accountName?.let {
                    mCredential.selectedAccountName = it

                    checkCalendarAndWriteEvent()
                }
                        ?: startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER_EVENT)
            } else {
                EasyPermissions.requestPermissions(
                        this,
                        "This app needs to access your Google account",
                        REQUEST_PERMISSION_ACCOUNT,
                        Manifest.permission.GET_ACCOUNTS)
            }

    @AfterPermissionGranted(REQUEST_WRITE_CALENDAR)
    private fun checkCalendarAndWriteEvent() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_CALENDAR)) {
            CalendarTask(this, Constants.CalendarTaskType.INSERT, mCredential, mEvent).execute()
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to write to your calendar",
                    REQUEST_PERMISSION_ACCOUNT,
                    Manifest.permission.WRITE_CALENDAR)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
        Log.e("Easy Permission", "Permission not Granted")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
        Log.e("Easy Permission", "Permission Granted")
    }

}