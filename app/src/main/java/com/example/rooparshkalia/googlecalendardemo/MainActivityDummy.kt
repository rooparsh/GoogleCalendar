package com.example.rooparshkalia.googlecalendardemo

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.util.Log
import android.view.View
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.config.GservicesValue.init
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import kotlinx.android.synthetic.main.activity_main.button
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class MainActivityDummy : Activity(), View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private lateinit var mCredential: GoogleAccountCredential

    companion object {
        private val CALENDAR_SCOPES = arrayListOf(CalendarScopes.CALENDAR)
        private const val REQUEST_PERMISSION_ACCOUNT = 1000
        private const val REQUEST_ACCOUNT_PICKER_EVENT = 1001
        private const val REQUEST_AUTHORISATION = 1002
        private const val REQUEST_WRITE_CALENDAR = 1003
        private const val REQUEST_GOOGLE_PLAY_SERVICES = 1004
        private const val PREF_ACCOUNT_NAME = "accountName"
        private const val CALENDAR_ID = "primary"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCredential = GoogleAccountCredential.usingOAuth2(this@MainActivityDummy, CALENDAR_SCOPES).setBackOff(ExponentialBackOff())

        button.text = "Click me to add event"
        button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> checkGooglePlayServices()
        }
    }

    private fun checkGooglePlayServices() {
        if (!isGooglePlayServicesAvailable()) {
            acquirePlayServices()
        } else {
            requestUserAccount()
        }
    }

    private fun acquirePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusResult = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (googleApiAvailability.isUserResolvableError(connectionStatusResult)) {
            showPlayServicesAvailabilityErrorDialog(connectionStatusResult)
        }
    }

    private fun showPlayServicesAvailabilityErrorDialog(connectionStatusResult: Int) {
        val googleApiAvailability = GoogleApiAvailability.getInstance()

        googleApiAvailability.getErrorDialog(this, connectionStatusResult, REQUEST_GOOGLE_PLAY_SERVICES).show()
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        return googleApiAvailability.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_ACCOUNT)
    private fun requestUserAccount() =
            if (EasyPermissions.hasPermissions(this@MainActivityDummy, Manifest.permission.GET_ACCOUNTS)) {
                val accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null)

                accountName?.let {
                    mCredential.selectedAccountName = it

                    checkCalendarAndWriteEvent()
                }
                        ?: startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER_EVENT)
            } else {
                EasyPermissions.requestPermissions(
                        this@MainActivityDummy,
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
            checkCalendarAndWriteEvent()
        }
    }

    @AfterPermissionGranted(REQUEST_WRITE_CALENDAR)
    private fun checkCalendarAndWriteEvent() {

        if (EasyPermissions.hasPermissions(this@MainActivityDummy, Manifest.permission.WRITE_CALENDAR)) {
            CalendarTask(this, mCredential).execute()
        } else {
            EasyPermissions.requestPermissions(
                    this@MainActivityDummy,
                    "This app needs to write to your calendar",
                    REQUEST_PERMISSION_ACCOUNT,
                    Manifest.permission.WRITE_CALENDAR)
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

                REQUEST_AUTHORISATION -> CalendarTask(this, mCredential).execute()
            }
        }
    }

    class CalendarTask internal constructor(context: Context, credential: GoogleAccountCredential) : AsyncTask<Unit, Unit, Unit>() {

        override fun doInBackground(vararg params: Unit?) {
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
        private var mContext: Context? = null


        init {
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()
            mContext = context
            mService = Calendar.Builder(transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar Demo")
                    .build()
        }

        private fun addEventToCalendar() {

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

            mService?.events()?.insert(CALENDAR_ID, event)?.execute()
        }

        override fun onCancelled() {
            if (mLastError != null) {
                if (mLastError is UserRecoverableAuthIOException) {
                    val e = mLastError as UserRecoverableAuthIOException
                    startActivityForResult(mContext as Activity, e.intent, MainActivityDummy.REQUEST_AUTHORISATION, null)
                    //MainActivityDummy().startActivityForResult(e.intent, MainActivityDummy.REQUEST_AUTHORISATION)
                } else {
                    mLastError!!.printStackTrace()
                }
            }
        }

    }
}
