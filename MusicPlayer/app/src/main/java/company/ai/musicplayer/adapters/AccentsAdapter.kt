package company.ai.musicplayer.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import company.ai.musicplayer.R
import company.ai.musicplayer.extensions.handleViewVisibility
import company.ai.musicplayer.mPreferences
import company.ai.musicplayer.utils.ThemeHelper

class AccentsAdapter(private val activity: Activity) :
        RecyclerView.Adapter<AccentsAdapter.AccentsHolder>() {

    private val mAccents = ThemeHelper.accents
    private var mSelectedAccent = mPreferences.accent

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccentsHolder {
        return AccentsHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_accent_item,
                        parent,
                        false
                )
        )
    }

    override fun getItemCount() = mAccents.size

    override fun onBindViewHolder(holder: AccentsHolder, position: Int) {
        holder.bindItems(mAccents[holder.adapterPosition])
    }

    inner class AccentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(position: Pair<Int, Pair<Int, Int>>) {
            itemView.apply {
                val circle: ImageButton = findViewById(R.id.circle)
                val textColor: TextView = findViewById(R.id.text_color)
                textColor.text = activity.getString(position.first)
                textColor.setTextColor(ContextCompat.getColor(activity, position.second.first))
                ThemeHelper.getColor(context, position.second.first, R.color.primary_deep_purple).apply {
                    ThemeHelper.updateTint(circle, this)
                    ThemeHelper.createColouredRipple(activity, this, R.drawable.ripple_rectangle)
                        .apply {
                            itemView.background = this
                        }
                }

                findViewById<ImageButton>(R.id.check).handleViewVisibility( position.second.first == mSelectedAccent)

                setOnClickListener {
                    if (position.second.first != mSelectedAccent) {
                        mSelectedAccent = position.second.first
                        mPreferences.accent = mSelectedAccent
                    }
                }
            }
        }
    }
}
