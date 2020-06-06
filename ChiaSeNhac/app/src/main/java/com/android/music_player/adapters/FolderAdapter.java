package com.android.music_player.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ItemViewHolder> {
    private final Activity mActivity;
    private Map<String, ArrayList<SongModel>> mFolders;
    private SharedPrefsUtils mSharedPrefsUtils;
    public FolderAdapter(Activity activity, Map<String, ArrayList<SongModel>> folders) {
        this.mFolders = folders;
        mActivity = activity;
        mSharedPrefsUtils = new SharedPrefsUtils(mActivity);
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_folder_line, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        List<String> keys = new ArrayList<>(mFolders.keySet());
        Collections.sort(keys);

        String folder = keys.get(position).split("/")[keys.get(position).split("/").length - 2];

        ArrayList<SongModel> music = mFolders.get(keys.get(position));
        holder.assignData(folder, music);
        holder.mLinearFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onClickItemListener.onClickMusic(mMusics.get(position).getSongName());
//                mSharedPrefsUtils.setString(Constants.PREFERENCES.SAVE_ALBUM_ID, mMusics.get(position).getAlbumID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFolders.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder  {

        private TextView mTextNameFolder, mTextInfoFolder;
        private LinearLayout mLinearFolder;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mLinearFolder = itemView.findViewById(R.id.item_ll_folder);
            mTextInfoFolder = itemView.findViewById(R.id.item_text_info_folder);
            mTextNameFolder = itemView.findViewById(R.id.item_text_title_folder);
        }

        public void assignData(final String folder, ArrayList<SongModel> models) {
            //UI setting code
            mTextNameFolder.setText(folder);
            mTextInfoFolder.setText(models.size()+ " bài hát");
        }
    }
}
