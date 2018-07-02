package com.example.rooparshkalia.googlecalendardemo

object Constants {

    const val CALENDAR_ID = "primary"
    const val REQUEST_AUTHORISATION_FOR_INSERTING = 1000
    const val REQUEST_AUTHORISATION_FOR_DELETING = 1001
    const val REQUEST_AUTHORISATION_FOR_UPDATING = 1002
    const val REQUEST_AUTHORISATION_FOR_GETTING = 1003
    const val REQUEST_AUTHORISATION_FOR_GETTING_ALL = 1004

    enum class CalendarTaskType {
        INSERT(),
        DELETE(),
        UPDATE(),
        GET(),
        GET_ALL()
    }

}