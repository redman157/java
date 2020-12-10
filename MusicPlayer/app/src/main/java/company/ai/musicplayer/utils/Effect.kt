package company.ai.musicplayer.utils


import android.view.View
import company.ai.musicplayer.R

object Effect{
    fun View.setEffect(boolean: Boolean){
        this.setBackgroundResource(R.drawable.selection_item)
    }
}