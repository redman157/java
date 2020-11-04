package com.android.music_player.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class AllPlayList {

    private Context mContext;
    private String TAG = "AllPlaylistLog";
    /* renamed from: db */
    private MusicOfPlayList mMusicOfPlayList;
    private ReaderSQL mDatabase;

    public AllPlayList(Context context){
        this.mContext = context;
        this.mDatabase = new ReaderSQL(context, Database.ALL_PLAY_LISTS.DATABASE_NAME, null, 1);
        mMusicOfPlayList = new MusicOfPlayList(context);
        mDatabase.queryData(Database.ALL_PLAY_LISTS.CREATE_TABLE);
        mDatabase.close();
    }

    public AllPlayList closeDatabase() {

        mDatabase.close();

        return this;
    }

    public boolean isExistData(Cursor cursor){
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                return true;
            }

        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        } finally {
            closeDatabase();
        }
        return false;
    }

    // TODO add row data base
    public void addRow(String namePlayList) {
        String SQL_INSERT =
                "INSERT INTO "+ Database.ALL_PLAY_LISTS.TABLE_NAME
                        +" VALUES(null"  + ","+
                        "'" + namePlayList +"'" + ")";

        mDatabase.queryData(SQL_INSERT);
        closeDatabase();
    }

    public boolean deletePlayList(String namePlayList) {
        String SQL_DELETE =  "DELETE FROM " +Database.ALL_PLAY_LISTS.TABLE_NAME
                + " WHERE "+Database.ALL_PLAY_LISTS.NAME_PLAY_LIST+ " = "+"'"+namePlayList+"'";

        if (search(namePlayList) && getSize() > 0){
            mDatabase.queryData(SQL_DELETE);
            mMusicOfPlayList.deletePlayList(namePlayList);
            closeDatabase();
            return true;
        }else {
            return false;
        }
    }

    public void deleteAllPlayList() {
        try {
            mDatabase.queryData(Database.ALL_MUSIC.DELETE);

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
    }

    public void updatePlayList(String namePlayListOri,String namePlayListChange){
        String SQL_UPDATE =
                "UPDATE "+
                        Database.ALL_PLAY_LISTS.TABLE_NAME+ " SET " +
                        Database.ALL_PLAY_LISTS.NAME_PLAY_LIST + "= '"+ namePlayListChange +"'" +
                        " WHERE "+ Database.ALL_PLAY_LISTS.NAME_PLAY_LIST + "= '"+ namePlayListOri+ "' ";
        mDatabase.queryData(SQL_UPDATE);
        closeDatabase();
    }

    public boolean search(String namePlayList){
        Cursor playListData = mDatabase.getData(Database.ALL_PLAY_LISTS.QUERY);
        try {
            if (isExistData(playListData)) {
                do {
                    if (playListData.getString(1).equals(namePlayList)) {

                        return true;
                    }
                }
                while (playListData.moveToNext());
            }
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }catch (CursorIndexOutOfBoundsException e) {
            // play list không có thì sẽ mặc định tạo
         /*   MediaManager.getInstance().setContext(mContext);
            MediaManager.getInstance().buildDataTheFirst();*/
            return  false;
        } finally {
            closeDatabase();
        }
        return false;
    }


    public ArrayList<String> getAllPlayList() {
        Cursor query = mDatabase.getData(Database.ALL_PLAY_LISTS.QUERY);
        ArrayList<String> allPlayList = new ArrayList<>();
        try {
            if (isExistData(query)) {
                do {
                    allPlayList.add(query.getString(1));
                } while (query.moveToNext());
            }
            return allPlayList;
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }catch (CursorIndexOutOfBoundsException e){
            return null;
        }
        finally {
            closeDatabase();
        }
        return null;
    }

    public int getSize() {
        Cursor cursor = mDatabase.getData(Database.ALL_PLAY_LISTS.QUERY);
        int count = 0;
        try {
            if (isExistData(cursor)) {
                count = cursor.getCount();
            }
            return count;
        } catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        } finally {
            closeDatabase();
        }
        return count;
    }

}
