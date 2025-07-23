package com.speech.main

import androidx.lifecycle.ViewModel
import com.speech.common_ui.event.EventHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val eventHelper: EventHelper,
) : ViewModel() {


}