package company.ai.musicplayer.controller

interface MediaControllerInterface{
    fun onCurrentPosition(pos : Int)
    fun onShuffle()
    fun onResumeOrPause()
    fun onCancelDialog()
    fun onEqualizer()
    fun onSkip(isNext: Boolean)
}