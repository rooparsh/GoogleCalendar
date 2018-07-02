package com.example.rooparshkalia.googlecalendardemo

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.DateTime
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class MainActivity : GoogleCalendarActivity(), View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private lateinit var mCredential: GoogleAccountCredential
    private lateinit var event: Event

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
        setContentView(R.layout.activity_main)

        mCredential = GoogleAccountCredential.usingOAuth2(this@MainActivity, CALENDAR_SCOPES).setBackOff(ExponentialBackOff())

        event = Event().setSummary("Test Event")
                .setLocation("Ludhiana")
                .setDescription("First Event in Ludhiana")

        val startDateTime = DateTime("2018-07-02T09:00:00Z")
        val timeZone = java.util.Calendar.getInstance().timeZone.getDisplayName(false, TimeZone.SHORT)
        val eventStartDateTime = EventDateTime().setDateTime(startDateTime).setTimeZone(timeZone)

        val endDateTime = DateTime("2018-07-02T10:00:00Z")
        val eventEndDateTime = EventDateTime().setDateTime(endDateTime).setTimeZone(timeZone)

        event.start = eventStartDateTime
        event.end = eventEndDateTime

        button.text = "Click me to add event"
        button.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                //checkGooglePlayServices()
                addEvent(event)
            }
        }
    }

//    private fun checkGooglePlayServices() {
//        if (!isGooglePlayServicesAvailable()) {
//            acquirePlayServices()
//        } else {
//            requestUserAccount()
//        }
//    }
//
//    private fun acquirePlayServices() {
//        val googleApiAvailability = GoogleApiAvailability.getInstance()
//        val connectionStatusResult = googleApiAvailability.isGooglePlayServicesAvailable(this)
//
//        if (googleApiAvailability.isUserResolvableError(connectionStatusResult)) {
//            showPlayServicesAvailabilityErrorDialog(connectionStatusResult)
//        }
//    }
//
//    private fun showPlayServicesAvailabilityErrorDialog(connectionStatusResult: Int) {
//        val googleApiAvailability = GoogleApiAvailability.getInstance()
//
//        googleApiAvailability.getErrorDialog(this, connectionStatusResult, REQUEST_GOOGLE_PLAY_SERVICES).show()
//    }
//
//    private fun isGooglePlayServicesAvailable(): Boolean {
//        val googleApiAvailability = GoogleApiAvailability.getInstance()
//        return googleApiAvailability.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
//    }
//
//    @AfterPermissionGranted(REQUEST_PERMISSION_ACCOUNT)
//    private fun requestUserAccount() =
//            if (EasyPermissions.hasPermissions(this@MainActivity, Manifest.permission.GET_ACCOUNTS)) {
//                val accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null)
//
//                accountName?.let {
//                    mCredential.selectedAccountName = it
//
//                    checkCalendarAndWriteEvent()
//                }
//                        ?: startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER_EVENT)
//            } else {
//                EasyPermissions.requestPermissions(
//                        this@MainActivity,
//                        "This app needs to access your Google account",
//                        REQUEST_PERMISSION_ACCOUNT,
//                        Manifest.permission.GET_ACCOUNTS)
//            }
//
//    private fun saveUserCredentials(data: Intent) {
//        val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
//        accountName?.let {
//            val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
//            val editor = sharedPreferences.edit()
//            editor.putString(PREF_ACCOUNT_NAME, it)
//            editor.apply()
//            mCredential.selectedAccountName = it
//            checkCalendarAndWriteEvent()
//        }
//    }
//
//    @AfterPermissionGranted(REQUEST_WRITE_CALENDAR)
//    private fun checkCalendarAndWriteEvent() {
//        if (EasyPermissions.hasPermissions(this@MainActivity, Manifest.permission.WRITE_CALENDAR)) {
//            CalendarTask(this, Constants.CalendarTaskType.INSERT, mCredential, event).execute()
//        } else {
//            EasyPermissions.requestPermissions(
//                    this@MainActivity,
//                    "This app needs to write to your calendar",
//                    REQUEST_PERMISSION_ACCOUNT,
//                    Manifest.permission.WRITE_CALENDAR)
//        }
//    }
//
//
//    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
//        Log.e("Easy Permission", "Permission not Granted")
//    }
//
//    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
//        Log.e("Easy Permission", "Permission Granted")
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == RESULT_OK) {
//            when (requestCode) {
//                REQUEST_ACCOUNT_PICKER_EVENT -> data?.let {
//                    data.extras?.let { saveUserCredentials(data) }
//                }
//                REQUEST_AUTHORISATION -> CalendarTask(this, Constants.CalendarTaskType.INSERT, mCredential, event).execute()
//            }
//        }
//    }
}
