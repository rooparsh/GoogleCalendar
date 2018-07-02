package com.example.rooparshkalia.googlecalendardemo

interface CalendarAsyncListener {
    fun onSuccess()

    fun onFailure(mLastError: Exception)
}