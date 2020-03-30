package com.cye.musicuitest.helpes

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

class MediaPlayerHelp private constructor(val context: Context ) {

    private val mediaPlayer:MediaPlayer = MediaPlayer()
    private var path:String? = null
    private lateinit var onMediaPlayerHelperListener:OnMediaPlayerHelperListener


    companion object : SingletonHolder<MediaPlayerHelp, Context>(::MediaPlayerHelp)

    fun setPath(path:String){
        //音乐正在播放或者切换音乐
        if(mediaPlayer.isPlaying || this.path!=path){
            mediaPlayer.reset()
        }
        this.path = path
        mediaPlayer.setDataSource(path)
        Log.d("MediaPlayerHelp》》》","最終文件的播放源:$path")
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onMediaPlayerHelperListener.onPrepared(it)
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
        mediaPlayer.release()
    }

    fun getDuration():Int{
        return mediaPlayer.duration
    }

    fun getCurrentPosition():Int{
        return mediaPlayer.currentPosition
    }

    interface OnMediaPlayerHelperListener{
        fun onPrepared(mp:MediaPlayer)
    }

    fun setOnMediaPlayerHelperListener(listener:OnMediaPlayerHelperListener){
        onMediaPlayerHelperListener = listener
    }

}