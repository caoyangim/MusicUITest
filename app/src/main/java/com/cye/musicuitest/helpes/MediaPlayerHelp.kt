package com.cye.musicuitest.helpes

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

class MediaPlayerHelp private constructor(val context: Context ) {

    private val mediaPlayer:MediaPlayer = MediaPlayer()
    private var path:String? = null
    private lateinit var onMediaPlayerHelperListener:OnMediaPlayerHelperListener
    private lateinit var onErrorListener:OnMediaPlayerHelperErrorListener
    private var isLooping=false

    companion object : SingletonHolder<MediaPlayerHelp, Context>(::MediaPlayerHelp)

    fun setPath(path:String){
        Log.e("mediaPlayer",mediaPlayer.toString()+":"+mediaPlayer.hashCode())
        //音乐正在播放或者切换音乐
        if(mediaPlayer.isPlaying || this.path!=path){
            mediaPlayer.reset()
        }
        this.path = path
        Log.d("MediaPlayerHelp》》》","最終文件的播放源:$path")
        try {
            mediaPlayer.setDataSource(path)
            mediaPlayer.prepareAsync()
        }catch (e:Exception){
            onErrorListener.onError("地址无效")
        }
        mediaPlayer.setOnPreparedListener {
            onMediaPlayerHelperListener.onPrepared(it)
        }
        mediaPlayer.setOnErrorListener { mp, what, extra ->
            Log.d("MediaPlayerHelp", "OnError - Error code: $what Extra code: $extra")
            onErrorListener.onError("地址无效，播放失败")
            false
        }
        if (isLooping){
            mediaPlayer.isLooping = isLooping
        }else{
            mediaPlayer.setOnCompletionListener {
                onMediaPlayerHelperListener.onCompelet(it)
            }
        }
    }


    fun getPath():String?{
        return path
    }

    fun start(){
        if (mediaPlayer.isPlaying) return
        mediaPlayer.start()
    }

    fun seekTo(pos:Int){
        mediaPlayer.seekTo(pos)
        mediaPlayer.start()
    }

    fun pause(){
        mediaPlayer.pause()
    }

    fun release(){
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
        path = null
    }

    fun reset(){
        mediaPlayer.reset()
    }

    fun getDuration():Int{
        return mediaPlayer.duration
    }

    fun getCurrentPosition():Int{
        return mediaPlayer.currentPosition
    }

    interface OnMediaPlayerHelperListener{
        fun onPrepared(mp:MediaPlayer)
        fun onCompelet(mp:MediaPlayer)
    }

    interface OnMediaPlayerHelperErrorListener{
        fun onError(msg:String)
    }

    fun setOnMediaPlayerHelperListener(listener:OnMediaPlayerHelperListener){
        onMediaPlayerHelperListener = listener
    }

    fun setOnMediaPlayerHelperErrorListener(listener: OnMediaPlayerHelperErrorListener){
        onErrorListener = listener
    }

    /**
     * 获取MediaPlayerId
     * 可视化类Visualizer需要此参数
     * @return  MediaPlayerId
     */
    fun getMediaPlayerId(): Int {
        return mediaPlayer.audioSessionId
    }

}