package com.cye.musicuitest

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.SeekBar
import com.cye.musicuitest.helpes.MediaPlayerHelp
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var mediaPlayerHelp:MediaPlayerHelp
    var isPlaying = false
    lateinit var path:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        initMediaPlay()
        initAnimator()


    }

    private fun initSeekBar() {
        seekbar.max = mediaPlayerHelp.getDuration()
        Log.e(">>>cao",""+seekbar.max)
        Timer().schedule(object : TimerTask(){
            override fun run() {
                seekbar.progress = mediaPlayerHelp.getCurrentPosition()
            }
        },0,100)
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaPlayerHelp.seekTo(seekBar?.progress!!)
                restartMusic()
            }

        })
    }

    private fun initMediaPlay() {
        val extSD = Environment.getExternalStorageDirectory().path
        path = "$extSD/netease/cloudmusic/Music/qifengle.m4a"
//        path = "$extSD/netease/cloudmusic/Music/张学友 - 约定 (live).mp3"
        mediaPlayerHelp = MediaPlayerHelp.getInstance(this)
    }

    private fun initAnimator() {
        btn_play.setOnClickListener{
            if (isPlaying){
                stopMusic()
            }else{
                playMusic(path)
            }
        }
    }

    private fun restartMusic(){
        playMusic(path)
    }

    private fun playMusic(path:String) {
        //音乐
        if (path == mediaPlayerHelp.getPath()){
            mediaPlayerHelp.start()
        }else{
            mediaPlayerHelp.setPath(path)
            mediaPlayerHelp.setOnMediaPlayerHelperListener(object : MediaPlayerHelp.OnMediaPlayerHelperListener{
                override fun onPrepared(mp: MediaPlayer) {
                    mediaPlayerHelp.start()
                    initSeekBar()
                }
            })
        }
        //动画
        platter_view.playAnimation()
        btn_play.setImageDrawable(resources.getDrawable(R.drawable.ic_suspend))
        isPlaying = true
    }

    private fun stopMusic() {
        //音乐
        mediaPlayerHelp.pause()
        //动画
        platter_view.pauseAnimation()
        btn_play.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
        isPlaying = false
    }

    override fun onStop() {
        super.onStop()
        mediaPlayerHelp.release()
    }

}
