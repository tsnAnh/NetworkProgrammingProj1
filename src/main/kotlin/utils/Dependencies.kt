package utils

import Payload
import com.google.gson.Gson

object Dependencies {
    private val gson = Gson()

    val payloadAdapter = gson.getAdapter(Payload::class.java)
}