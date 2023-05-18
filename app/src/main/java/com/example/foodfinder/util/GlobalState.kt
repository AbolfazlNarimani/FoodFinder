package com.example.foodfinder.util

object GlobalState {

    private const val STATE_UNKNOWN = "Unknown"
    private const val STATE_LOADING = "Loading"
    const val STATE_SUCCESSFUL = "Successful"
    private const val STATE_ERROR = "Error"


    var state = STATE_UNKNOWN

    fun setStateLoading() {
        state = STATE_LOADING
    }

    fun setStateSuccessful() {
        state = STATE_SUCCESSFUL
    }

    fun setStateError() {
        state = STATE_ERROR
    }

}