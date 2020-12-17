package company.ai.musicplayer

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MusicsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val icon: ImageView = itemView.findViewById(R.id.image_icon)
    val title: TextView = itemView.findViewById(R.id.text_title)
    val duration: TextView = itemView.findViewById(R.id.text_suptitle)
    val subtitle: TextView = itemView.findViewById(R.id.text_sub_title)
}

class GenericViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.text_title)
    val subtitle: TextView = itemView.findViewById(R.id.text_sub_title)
    val icon: ImageView = itemView.findViewById(R.id.image_icon)
}

class AlbumArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.text_title)
    val subtitle: TextView = itemView.findViewById(R.id.text_sub_title)
    val icon: ImageView = itemView.findViewById(R.id.image_icon)
}

class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.text_title)
}