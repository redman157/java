package company.ai.musicplayer.controller

interface MediaControllerInterface{
    fun onCurrentPosition(pos : Int)
    fun onDismissDialog()
}