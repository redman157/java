package com.example.stockchart2

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder

interface Renderer {
    fun onAssignNotifying(notify: Notifier?)
    fun onGenerate(view: View?): TypeViewHolder?
    fun onBinding(holder: ViewHolder?, position: Int)
    fun onCounting(): Int
}