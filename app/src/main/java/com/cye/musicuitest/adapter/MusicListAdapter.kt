package com.cye.musicuitest.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.cye.musicuitest.R
import com.cye.musicuitest.bean.Song
import kotlinx.android.synthetic.main.item_music_list.view.*

class MusicListAdapter(data: MutableList<Song>?) :
    BaseQuickAdapter<Song, BaseViewHolder>(R.layout.item_music_list, data) {

    override fun convert(holder: BaseViewHolder, item: Song) {
        holder.setText(R.id.item_music,item.song)
    }

}