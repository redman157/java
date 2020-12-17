package company.ai.musicplayer.controller

interface MediaControllerInterface{
    fun onCurrentPosition(pos : Int)
    fun onResumeOrPause()
    fun onCancelDialog()
    fun onSkip(isNext: Boolean)
}