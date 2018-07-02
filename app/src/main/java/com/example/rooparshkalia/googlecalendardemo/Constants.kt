package com.example.rooparshkalia.googlecalendardemo

object Constants {

    const val CALENDAR_ID = "primary"
    const val TYPE = "type"
    const val REQUEST_AUTHORISATION = 1111

    enum class CalendarTaskType {
        INSERT(),
        DELETE(),
        UPDATE(),
        GET(),
        GET_ALL()
    }

}