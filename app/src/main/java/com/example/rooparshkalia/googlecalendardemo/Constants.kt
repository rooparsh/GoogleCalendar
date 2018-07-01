package com.example.rooparshkalia.googlecalendardemo

object Constants {

    const val CALENDAR_ID = "primary"
    const val REQUEST_AUTHORISATION = 1002

    enum class CalendarTaskType {
        INSERT(),
        DELETE(),
        UPDATE(),
        GET(),
        GET_ALL()
    }

}