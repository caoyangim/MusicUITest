package com.cye.musicuitest

import android.Manifest
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.media.audiofx.Visualizer.OnDataCaptureListener
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cye.musicuitest.helpes.MediaPlayerHelp
import com.cye.musicuitest.utils.MusicUtil
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.system.exitProcess
import android.widget.PopupWindow
import android.view.Gravity
import android.content.Context
import android.view.LayoutInflater
import android.graphics.drawable.BitmapDrawable
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cye.musicuitest.adapter.MusicListAdapter
import com.cye.musicuitest.bean.Song


class MainActivity : AppCompatActivity() {

    lateinit var mediaPlayerHelp:MediaPlayerHelp
    private var isPlaying = false
    lateinit var path:String
    private val timer = Timer()
    private var visualizer:Visualizer? = null
    private var isMenu = false
    private lateinit var musicData:MutableList<Song>

    private val PERMISSIONS = arrayOf<String>(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.MODIFY_AUDIO_SETTINGS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        initView()
        initData()
        initMediaPlay()
        initAnimator()
        initVisualizer()
    }

    private fun initData() {
        musicData = MusicUtil.getMusicData(this)
        Log.e("Music",","+musicData[0].toString()+musicData.size)
        btn_list.setOnClickListener {
            showPopupWindow()
        }
    }

    private fun initView() {
        //隐藏ActionBar
        supportActionBar?.hide()
        //隐藏状态栏
        //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        toolbar.setNavigationIcon(R.drawable.ic_back)
        //背景模糊
        Glide.with(this).load(R.mipmap.zhang)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 50)))
            .into(main_bg)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.Ancient -> effect.setAncientEffectDrawable()
                R.id.BlastBass -> effect.setBlastBassEffectDrawable()
                R.id.Electronic -> effect.setElectronicEffectDrawable()
                R.id.Histogram -> effect.setHistogramEffectDrawablee()
                R.id.Lonely -> effect.setLonelyEffectDrawable()
                R.id.Reverberation -> effect.setReverberationEffectDrawable()
                R.id.Surround -> effect.setSurroundEffectDrawable()
                R.id.Valve -> effect.setValveEffectDrawable()
            }
            true
        }
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
  //      path = "$extSD/Music/qifengle2.m4a"
        path = "$extSD/netease/cloudmusic/Music/张学友 - 约定 (live).mp3"
        mediaPlayerHelp = MediaPlayerHelp.getInstance(this)
        mediaPlayerHelp.setOnMediaPlayerHelperErrorListener(object : MediaPlayerHelp.OnMediaPlayerHelperErrorListener{
            override fun onError(msg: String) {
                Toast.makeText(applicationContext,msg,Toast.LENGTH_SHORT).show()
            }
        })
        play()
    }

    private fun initAnimator() {
        btn_play.setOnClickListener{
            if (isPlaying){
                stop()
            }else{
                play()
            }
        }
    }

    private fun stop() {
        stopMusic()
        stopAnima()
        isPlaying = false
    }

    private fun play() {
        playMusic(path)
        playAnima()
        isPlaying = true
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
    }


    private fun stopMusic() {
        //音乐
        mediaPlayerHelp.pause()
    }

    private fun stopAnima(){
        //动画
        platter_view.pauseAnimation()
        btn_play.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
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
            effect.onWaveCall(waveform)
        }

        override fun onFftDataCapture(visualizer: Visualizer, fft: ByteArray, samplingRate: Int) {
            //audioView2.post(Runnable { audioView2.setWaveData(fft) })
            effect.onCall(fft)
        }
    }

    private fun initVisualizer() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
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
        effect.setSurroundEffectDrawable()
    }

    private var popupWindow: PopupWindow? = null
    private fun showPopupWindow() {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val vPopupWindow = inflater.inflate(R.layout.pop_music_list, null, false)//引入弹窗布局
        val rcView = vPopupWindow.findViewById<RecyclerView>(R.id.music_list)

        popupWindow = PopupWindow(
            vPopupWindow,
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT,
            true
        )

        //设置背景透明
       // addBackground()
        //设置进出动画
        popupWindow?.animationStyle = R.style.PopupWindowAnimation

        popupWindow?.setBackgroundDrawable(resources.getDrawable(R.drawable.pop_bac)) // 响应返回键必须的语句
        //引入依附的布局
        val parentView =
            LayoutInflater.from(this@MainActivity).inflate(R.layout.activity_main, null)
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        popupWindow?.showAtLocation(parentView, Gravity.BOTTOM, 0, 100)

        rcView.layoutManager = LinearLayoutManager(this)
        rcView.adapter = MusicListAdapter(musicData)
    }

    private fun addBackground() {
        // 设置背景颜色变暗
        val lp:WindowManager.LayoutParams = window.attributes
        lp.alpha = 0.1f
        window.attributes = lp
        //dismiss时恢复原样
        popupWindow?.setOnDismissListener {
            val lp1:WindowManager.LayoutParams = window.attributes
            lp1.alpha = 1f
            window.attributes = lp
        }
    }

}
