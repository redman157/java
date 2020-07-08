package com.android.music_player.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.models.MusicModel;
import com.android.music_player.utils.ImageHelper;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ItemViewHolder> {
    private final Activity mActivity;
    private Map<String, ArrayList<MusicModel>> mArtists;
    private SharedPrefsUtils mSharedPrefsUtils;
    private List<String> keys;
    public ArtistAdapter(Activity activity, Map<String, ArrayList<MusicModel>> artists) {
        this.mArtists = artists;
        mActivity = activity;
        keys = new ArrayList<>(mArtists.keySet());
        Collections.sort(keys);
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
        String item = keys.get(position);
        ArrayList<MusicModel> music = mArtists.get(item);

        holder.assignData(item, music);
        holder.mLinearArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onClickItemListener.onChooseMedia(mArtists.get(position).getMusicId());
//                mSharedPrefsUtils.setString(Constants.PREFERENCES.SAVE_ALBUM_ID, mArtists.get(position).getAlbumID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder  {
        private ImageView mImgArtist;
        private TextView mTextArtist, mTextInfoArtist;
        private LinearLayout mLinearArtist;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mLinearArtist = itemView.findViewById(R.id.item_ll_artist);
            mTextInfoArtist = itemView.findViewById(R.id.item_text_info_artist);
            mImgArtist = itemView.findViewById(R.id.item_img_artist);
            mTextArtist = itemView.findViewById(R.id.item_text_title_artist);
        }

        @SuppressLint("SetTextI18n")
        public void assignData(String artist, ArrayList<MusicModel> models) {
            //UI setting code
            mTextArtist.setText(artist);
            mTextInfoArtist.setText(models.size() +" bài hát");
            mImgArtist.setClipToOutline(true);
            ImageHelper.getInstance(mActivity).getSmallImageByPicasso(models.get(0).getAlbumID(),
                    mImgArtist);
        }
    }
}
