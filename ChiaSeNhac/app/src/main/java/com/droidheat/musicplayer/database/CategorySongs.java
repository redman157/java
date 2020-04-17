package com.droidheat.musicplayer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class CategorySongs {

    private Context context;
    private String TAG = "CategorySongsLog";
    /* renamed from: db */
    private SQLiteDatabase database;
    private ReaderSQL mCategory;


    public CategorySongs(Context context){
        this.context = context;
        mCategory = new ReaderSQL(context, Database.CATEGORY.DATABASE_NAME, null, 1 );
        mCategory.queryData(Database.CATEGORY.SQL_CREATE_ENTRIES);
    }


    public CategorySongs closeDatabase() {
        this.mCategory.close();
        return this;
    }

    private String dropInvalidString(String str) {
        return str.replaceAll("[^A-Za-z0-9()\\[\\]]", "");
    }

    public boolean isSelect(Cursor cursor){
        cursor = mCategory.getData(Database.CATEGORY.QUERY);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                return true;
            }
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
        return false;
    }

    // add Data
    public void addCategory(SongModel song) {
        String SQL_INSERT = "INSERT INTO "+ Database.CATEGORY.TABLE_NAME+
                " Value(null, " +
                "'"+ song.get_ID() +"'"      +","+
                "'"+ "0" +"'" +","+
                "'"+ song.getSongName() +"'" +","+
                "'"+ song.getPath() +"'"     +","+
                "'"+ song.getArtist() +"'"   +","+
                "'"+ song.getAlbum() +"'"    +","+
                "'"+ song.getAlbumID() +"'"  +","+
                "'"+ song.getFileName()+"'"  +","+
                "'"+ song.getTime()+"'"  +","+
                "'"+ dropInvalidString(song.getPath())+"'"      +")";

        mCategory.queryData(SQL_INSERT);
        Toast.makeText(context, "Đã Add Bài Hát : "+song.getSongName(), Toast.LENGTH_SHORT).show();
        closeDatabase();

    }

    // lấy hết toàn bộ data
    public ArrayList<SongModel> getAllCategory() {
        Cursor data = mCategory.getData(Database.CATEGORY.QUERY);

        ArrayList<SongModel> mSongs = new ArrayList<>();

        if (isSelect(data)){
            while (!data.isAfterLast()){
                SongModel.Builder builder = new SongModel.Builder();
                builder.setSongName(data.getString(3));
                builder.setPath(data.getString(4));
                builder.setArtist(data.getString(5));
                builder.setAlbum(data.getString(6));
                builder.setAlbumID(data.getString(7));
                builder.setFileName(data.getString(9));
                builder.setID(data.getString(1));
                builder.setTime(data.getInt(8));

                SongModel songModel = builder.generate();
                mSongs.add(songModel);
            }
        }
        closeDatabase();
        return mSongs;
    }

    // tìm category
    public boolean searchCategory(long category){
        Cursor mCategoryData = mCategory.getData(Database.CATEGORY.QUERY);
        try {
            if (isSelect(mCategoryData) && getSize() != 0){
                while (mCategoryData.moveToNext()){
                    if (mCategoryData.getString(1).equals(String.valueOf(category))){
                        return true;
                    }
                }
            }
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
        return false;
    }

    // xóa category theo id
    public void deleteCategory(int id){
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.CATEGORY.TABLE_NAME+" WHERE id= '"+id+ "' ";
        mCategory.queryData(SQL_DELETE);
        closeDatabase();
    }

    // xóa category theo category
    public boolean deleteCategory(long category) {
        String id = String.valueOf(category);
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.CATEGORY.TABLE_NAME+" WHERE " +
                        "category= " +
                        "'"+id+ "' ";
        Cursor data = mCategory.getData(Database.CATEGORY.QUERY);

        if (isSelect(data)){
            if (searchCategory(category)){
                mCategory.queryData(SQL_DELETE);
                Toast.makeText(context, "Xóa Thành Công Thể Loại: "+category,
                        Toast.LENGTH_SHORT).show();
                closeDatabase();
                return true;
            }else {
                Toast.makeText(context, "Xóa Không Thành Công Thể Loại: "+category,
                        Toast.LENGTH_SHORT).show();

                return false;
            }
        }else {
            return false;
        }
    }

    // xóa tất cả
    public void deleteAll(int id) {
        try {
            mCategory.queryData(Database.CATEGORY.DELETE);

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
    }


    // kích thước data
    public int getSize() {
        Cursor songData = mCategory.getData(Database.CATEGORY.QUERY);
        int count = 0;
        try {
            if (isSelect(songData)) {
                count = songData.getCount();
            }
        } catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        } finally {
            closeDatabase();
        }
        return count;
    }

    public void updateCategory(String fake_path, SongModel song){
        String SQL_UPDATE = "UPDATE "+ Database.CATEGORY.TABLE_NAME+ " SET "+
                Database.CATEGORY.NAME_CATEGORY + "='"+ song.getSongName()+"'" + "," +
                Database.CATEGORY.PATH          + "='"+ song.getPath()+"'"     + "," +
                Database.CATEGORY.ARTIST        + "='"+ song.getArtist()+"'"   + "," +
                Database.CATEGORY.ALBUM         + "='"+ song.getAlbum()+"'"    + "," +
                Database.CATEGORY.ALBUM_ID      + "='"+ song.getAlbumID()+"'"  + "," +
                Database.CATEGORY.FILE_NAME     + "='"+ song.getFileName()+"'" + "," +
                Database.CATEGORY.CATEGORY      + "='"+ song.get_ID()+"'"      + "," +
                Database.CATEGORY.TIME          + "='"+ song.getTime()+"'" +
                " WHERE " + "fake_path= '"+ fake_path +"'";
        mCategory.queryData(SQL_UPDATE);
        closeDatabase();
    }



}
