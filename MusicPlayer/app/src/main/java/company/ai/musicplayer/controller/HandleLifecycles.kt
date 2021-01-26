package company.ai.musicplayer.controller

import android.content.Context
import android.location.Location

class HandleLifecyclesInterface(context: Context, callBack: (Location) -> Unit ){
    fun start(){}
    fun stop(){}
}