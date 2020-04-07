package com.cye.musicuitest.bean

class Song {
    /**
     * 歌手
     */
    var singer:String = ""
    /**
     * 歌曲名
     */
    var song:String = ""
    /**
     * 歌曲的地址
     */
    var path:String = ""
    /**
     * 歌曲长度
     */
    var duration:Int = 0
    /**
     * 歌曲的大小
     */
    var size:Long = 0

    var artId:Int = 0

    var albumArt:String? = null

    override fun toString(): String {
        return "song:$song,singer:$singer,path:$path,duration:$duration,size:$size,artId:${artId},ARTIOCN:$albumArt"
    }
}