package com.ksajja.newone

/**
 * Created by ksajja on 1/30/18.
 */

//TODO Remove constants and create a object 'Date'
object Constants {

    const val DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val DISPLAY_DATE_FORMAT = "yyyy/MM/dd"
    const val DATE_AND_TIME = "dd MMM, yyyy @ h:mm a"
    const val DISPLAY_TIME_FORMAT = "h:mm a"
    const val DISPLAY_WEEKDAY = "EEEE"

    object Keys {
        const val TASK_ID = "taskId"
        const val LIST_ID = "listId"
        const val LIST = "list"
    }

    object SharedPrefKeys {
        const val DEVICE_TOKEN = "deviceToken"
        const val AUTH_TOKEN = "authToken"
        const val UNIQUE_DEVICE_ID = "uniqueDeviceId"
        const val SHOULD_SHOW_ONBOARDING = "onboarding"
        const val ksajja_ID = "ksajjaId"
    }

    object TemplateTypes {
        val PRODUCT_SUGGESTION= "ProductSuggestions"
        val WEB_ARTICLES= "WebArticles"
        val INFORMATION_VIDEOS= "InformationalVideos"
        val SERVICES= "Services"
        val SPONSORED_PARTNER= "SponsoredPartners"
        val QUESTION= "Question"
        val INAPPROPRIATE= "Inappropriate"
        val CANT_HELP= "CantHelp"
        val COMMENTARY= "Commentary"
    }
}
