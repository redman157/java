package com.android.music_player.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.adapters.AlbumAdapter;
import com.android.music_player.adapters.ArtistAdapter;
import com.android.music_player.adapters.FolderAdapter;
import com.android.music_player.adapters.MusicAdapter;
import com.android.music_player.adapters.ViewPagerAdapter;
import com.android.music_player.interfaces.DialogType;
import com.android.music_player.interfaces.OnClickItemListener;
import com.android.music_player.interfaces.OnConnectMediaId;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.models.MusicModel;
import com.android.music_player.utils.BottomSheetHelper;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageHelper;
import com.android.music_player.utils.SharedPrefsUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class LibraryFragment extends Fragment implements View.OnClickListener,
        ViewPager.OnPageChangeListener,
        TabLayout.OnTabSelectedListener, MusicAdapter.OnClickListener, OnConnectMediaId {
    private MediaManager mMediaManager;

//    private Toolbar mToolBar;
    private SharedPrefsUtils mSharedPrefsUtils;
    public ImageView mProfile, mBackGround;
    public TextView TextProfileTitle, TextProfileArtist, TextProfileAlbum;
    private TabLayout mTabLayoutSong;
    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPagerSong;
    private View collapsingProfileHeaderView;
    private String songName;
    private View view;
    private MusicAdapter mMusicAdapter;
    private FolderAdapter mFolderAdapter;
    private AlbumAdapter mAlbumAdapter;
    private ArtistAdapter mArtistAdapter;
    private ImageView mImgBack;
    private HomeActivity mHomeActivity;
    private static LibraryFragment fragment = null;
    public static LibraryFragment newInstance() {
        if (fragment == null){
            fragment = new LibraryFragment();

        }
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("AAA","LibraryFragment: context: "+context.getClass().getSimpleName());
        //is called when a fragment is connected to an activity.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeActivity = (HomeActivity) getActivity();
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(getContext());
        mSharedPrefsUtils = new SharedPrefsUtils(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_library, container, false);
        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // is called after onCreateView()
        // and ensures that the fragment's root view is non-null. Any view setup should happen here. E.g., view lookups, attaching listeners.
        // Setup any handles to view objects here
        initView(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // is called when host activity has completed its onCreate() method.
//        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolBar);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupViewPager(mViewPagerSong);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setAllAdapter(){
        mMusicAdapter = new MusicAdapter(getActivity(), MusicLibrary.music, true);
        mMusicAdapter.notifyDataSetChanged();
        mMusicAdapter.setOnConnectMediaIdListener(this);

        mArtistAdapter = new ArtistAdapter(getActivity(), mMediaManager.getArtist());
        mArtistAdapter.notifyDataSetChanged();
        mArtistAdapter.setOnConnectMediaIdListener(this);

        mAlbumAdapter = new AlbumAdapter(getActivity(), mMediaManager.getAlbum());
        mAlbumAdapter.notifyDataSetChanged();
        mAlbumAdapter.setOnConnectMediaIdListener(this);

        mFolderAdapter = new FolderAdapter(getActivity(), mMediaManager.getFolder() );
        mFolderAdapter.notifyDataSetChanged();
        mFolderAdapter.setOnConnectMediaIdListener(this);
    }

    private void setupViewPager(ViewPager viewPager){
        setAllAdapter();
        if (mViewPagerAdapter == null) {
            mViewPagerAdapter = new ViewPagerAdapter(getContext(), getActivity().getSupportFragmentManager());
            mViewPagerAdapter.addFragment(new ListMusicFragment(mMusicAdapter));
            mViewPagerAdapter.addFragment(new ListArtistFragment(mArtistAdapter));
            mViewPagerAdapter.addFragment(new ListAlbumFragment(mAlbumAdapter));
            mViewPagerAdapter.addFragment(new ListFolderFragment(mFolderAdapter));

            viewPager.setAdapter(mViewPagerAdapter);
            viewPager.setCurrentItem(0);
            viewPager.addOnPageChangeListener(this);
            viewPager.setOnClickListener(this);
            mTabLayoutSong.setupWithViewPager(viewPager);
            for (int i = 0; i < mTabLayoutSong.getTabCount(); i++) {
                mTabLayoutSong.getTabAt(i).setCustomView(mViewPagerAdapter.getTabSong(i));
            }
            mTabLayoutSong.addOnTabSelectedListener(this);
        }
    }

    private void initView(View view) {
        collapsingProfileHeaderView = view.findViewById(R.id.collapseActionView);
        mViewPagerSong = view.findViewById(R.id.vp_AllMusic);
        mTabLayoutSong = view.findViewById(R.id.tab_AllMusic);
        TextProfileAlbum = collapsingProfileHeaderView.findViewById(R.id.profileMisc);
        mProfile = collapsingProfileHeaderView.findViewById(R.id.profileImage);
        TextProfileTitle = collapsingProfileHeaderView.findViewById(R.id.profileName);
        TextProfileArtist = collapsingProfileHeaderView.findViewById(R.id.profileSubtitle);
        mBackGround = view.findViewById(R.id.img_AlbumId);
        setTitle(mMediaManager.getCurrentMusic());
    }

    public void setTitle(String mediaID){
        if (mediaID.equals("")){
            mediaID = (String) MusicLibrary.music.keySet().toArray()[0];
        }
        MediaMetadataCompat metadataCompat = mMediaManager.getMetadata(getContext(), mediaID);
        TextProfileAlbum.setText(metadataCompat.getString(Constants.METADATA.Album));
        TextProfileTitle.setText(metadataCompat.getString(Constants.METADATA.Title));
        TextProfileArtist.setText(metadataCompat.getString(Constants.METADATA.Artist));

        ImageHelper.getInstance(getContext()).getSmallImageByPicasso(
                String.valueOf(MusicLibrary.getAlbumRes(mediaID)), mProfile);
        ImageHelper.getInstance(getContext()).getSmallImageByPicasso(
                String.valueOf(MusicLibrary.getAlbumRes(mediaID)), mBackGround);
        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity)getActivity()).mTextTitleMedia.setText(metadataCompat.getString(Constants.METADATA.Title));
            ((HomeActivity)getActivity()).mTextArtistMedia.setText(metadataCompat.getString(Constants.METADATA.Artist));
            ImageHelper.getInstance(getContext()).getSmallImageByPicasso(
                    String.valueOf(MusicLibrary.getAlbumRes(mediaID)), ((HomeActivity)getActivity()).mImgAlbumArt);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_back:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.relative_info_music:
                break;
            case R.id.imbt_play_media:
//                Utils.isPlayMediaService(this, mMediaManager.getType(), mMediaManager.getPosition());
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        for (int i = 0; i < mTabLayoutSong.getTabCount(); i ++){
            View view = mTabLayoutSong.getTabAt(i).getCustomView();
            TextView title = view.findViewById(R.id.item_tl_text_home);
            int color = (i == tab.getPosition()) ? getResources().getColor(R.color.red) :
                    getResources().getColor(R.color.white);
            title.setTextColor(color);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onClick(String type, int position) {

    }

    @Override
    public void onChangeMediaId(String mediaID) {
        setTitle(mediaID);
        ((HomeActivity)getActivity()).getControllerActivity().getTransportControls().prepareFromMediaId(mediaID, null);
    }

    @Override
    public void onChangeFlowType(String type, String title) {
        if (type.equals(Constants.VALUE.MUSIC)){

        }else if (type.equals(Constants.VALUE.ALBUM)){
            mHomeActivity.bottomSheetHelper = new BottomSheetHelper(DialogType.CHOOSE_ITEM_LIBRARY,
                    MusicLibrary.album, new OnClickItemListener() {
                @Override
                public void onAddMusicToPlayList(String namePlayList) {

                }

                @Override
                public void onChooseItemLibrary(ArrayList<MusicModel> models) {

                }
            });
            mHomeActivity.bottomSheetHelper.setTitle("All Album In Device");
            mHomeActivity.bottomSheetHelper.show(mHomeActivity.getSupportFragmentManager(),mHomeActivity.FRAGMENT_TAG);
        }else if (type.equals(Constants.VALUE.ARTIST)){

        }else if (type.equals(Constants.VALUE.FOLDER)){

        }
    }
}
