package com.cye.musicuitest

import android.Manifest
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.media.audiofx.Visualizer.OnDataCaptureListener
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cye.musicuitest.helpes.MediaPlayerHelp
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    lateinit var mediaPlayerHelp:MediaPlayerHelp
    var isPlaying = false
    lateinit var path:String
    private val timer = Timer()
    private var visualizer:Visualizer? = null
    private val PERMISSIONS = arrayOf<String>(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.MODIFY_AUDIO_SETTINGS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        initView()
        initMediaPlay()
        initAnimator()
        initVisualizer()

    }

    private fun initView() {
        //隐藏ActionBar
        supportActionBar?.hide()
        //隐藏状态栏
        //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        Glide.with(this).load(R.mipmap.zhang)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 50)))
            .into(main_bg)
    }

    private fun initSeekBar() {
        seekbar.max = mediaPlayerHelp.getDuration()
        Log.e(">>>cao",""+seekbar.max)

        timer.schedule(object : TimerTask(){
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
//        path = "$extSD/netease/cloudmusic/Music/qifengle.m4a"
        path = "$extSD/netease/cloudmusic/Music/张学友 - 约定 (live).mp3"
        mediaPlayerHelp = MediaPlayerHelp.getInstance(this)
    }

    private fun initAnimator() {
        btn_play.setOnClickListener{
            if (isPlaying){
                stopMusic()
                stopAnima()
            }else{
                playMusic(path)
                playAnima()
            }
        }
    }

    private fun restartMusic(){
        playMusic(path)
        //暂停/播放状态下拖动seekbar都能照常旋转
        stopAnima()
        playAnima()
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
    }

    private fun playAnima(){
        //动画
        platter_view.playAnimation()
        btn_play.setImageDrawable(resources.getDrawable(R.drawable.ic_suspend))
        isPlaying = true
    }


    private fun stopMusic() {
        //音乐
        mediaPlayerHelp.pause()
    }

    private fun stopAnima(){
        //动画
        platter_view.pauseAnimation()
        btn_play.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
        isPlaying = false
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        mediaPlayerHelp.release()
        exitProcess(0)
    }


    private val dataCaptureListener: OnDataCaptureListener = object : OnDataCaptureListener {
        override fun onWaveFormDataCapture( visualizer: Visualizer, waveform: ByteArray, samplingRate: Int) {
            //audioView.post(Runnable { audioView.setWaveData(waveform) })
        }

        override fun onFftDataCapture(visualizer: Visualizer, fft: ByteArray, samplingRate: Int) {
            //audioView2.post(Runnable { audioView2.setWaveData(fft) })
        }
    }

    private fun initVisualizer() {
        try {
            val mediaPlayerId: Int = mediaPlayerHelp.getMediaPlayerId()
            visualizer?.release()
            visualizer = Visualizer(mediaPlayerId)
            /**
             * 可视化数据的大小： getCaptureSizeRange()[0]为最小值，getCaptureSizeRange()[1]为最大值
             */
            val captureSize = Visualizer.getCaptureSizeRange()[1]
            val captureRate = Visualizer.getMaxCaptureRate() * 3 / 4
            visualizer?.setCaptureSize(captureSize)
            visualizer?.setDataCaptureListener(dataCaptureListener, captureRate, true, true)
            visualizer?.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED)
            visualizer?.setEnabled(true)
        } catch (e: Exception) {
            Log.e("initVisualizer Error", "请检查录音权限")
        }
    }

}
