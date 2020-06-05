package com.android.music_player.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Map;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ItemViewHolder> {
    private final Activity mActivity;
    private Map<String, ArrayList<SongModel>> mArtists;
    private SharedPrefsUtils mSharedPrefsUtils;
    public ArtistAdapter(Activity activity, Map<String, ArrayList<SongModel>> artists) {
        this.mArtists = artists;
        mActivity = activity;
        mSharedPrefsUtils = new SharedPrefsUtils(mActivity);
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_artist_line, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        MusicManager.getInstance().setContext(mActivity);
        ArrayList<SongModel> model = new ArrayList<>(MusicLibrary.info);
        final SongModel item = model.get(position);
        ArrayList<SongModel> music = mArtists.get(item.getArtist());
        Log.d("YYY","ArtistAdapter ---  onBindViewHolder: "+ item +" --- size: "+music.size() );
        holder.assignData(item, music);

        holder.mLinearArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onClickItemListener.onClickMusic(mArtists.get(position).getSongName());
//                mSharedPrefsUtils.setString(Constants.PREFERENCES.SAVE_ALBUM_ID, mArtists.get(position).getAlbumID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder  {
        private ImageView mImgArtist;
        private TextView mTextArtist, mTextInfoAritst;
        private LinearLayout mLinearArtist;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mLinearArtist = itemView.findViewById(R.id.item_ll_artist);
            mTextInfoAritst = itemView.findViewById(R.id.item_text_info_artist);
            mImgArtist = itemView.findViewById(R.id.item_img_artist);
            mTextArtist = itemView.findViewById(R.id.item_text_artist);
        }

        @SuppressLint("SetTextI18n")
        public void assignData(SongModel songModel, ArrayList<SongModel> models) {
            //UI setting code
            mTextArtist.setText(songModel.getArtist());
            mTextInfoAritst.setText(models.size() +" bài hát");
            mImgArtist.setClipToOutline(true);
            ImageUtils.getInstance(mActivity).getSmallImageByPicasso(songModel.getAlbumID(), mImgArtist);
        }
    }
}
