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
import com.android.music_player.interfaces.OnConnectMediaId;
import com.android.music_player.models.MusicModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ItemViewHolder> {
    private final Activity mActivity;
    private Map<String, ArrayList<MusicModel>> mFolders;
    private SharedPrefsUtils mSharedPrefsUtils;
    private List<String> keys;
    public FolderAdapter(Activity activity, Map<String, ArrayList<MusicModel>> folders) {
        this.mFolders = folders;
        keys = new ArrayList<>(mFolders.keySet());
        Collections.sort(keys);
        mActivity = activity;
        mSharedPrefsUtils = new SharedPrefsUtils(mActivity);
    }

    private OnConnectMediaId onConnectMediaId;
    public void setOnConnectMediaIdListener(OnConnectMediaId onConnectMediaId){
        this.onConnectMediaId = onConnectMediaId;
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
        final String folder = keys.get(position).split("/")[keys.get(position).split("/").length - 2];

        ArrayList<MusicModel> music = mFolders.get(keys.get(position));
        holder.assignData(folder, music);
        holder.mLinearFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConnectMediaId.onChangeFlowType(Constants.VALUE.FOLDER, folder);
//                onClickItemListener.onChooseMedia(mMusics.get(position).getMusicId());
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

        public void assignData(final String folder, ArrayList<MusicModel> models) {
            //UI setting code
            mTextNameFolder.setText(folder);
            mTextInfoFolder.setText(models.size()+ " bài hát");
        }
    }
}
