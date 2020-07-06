package com.android.music_player.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.android.music_player.managers.MediaManager;
import com.android.music_player.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class AllPlayList {

    private Context mContext;
    private String TAG = "AllPlaylistLog";
    /* renamed from: db */
    private RelationMusic mRelationMusic;
    private ReaderSQL mDatabase;

    public AllPlayList(Context context){
        this.mContext = context;
        this.mDatabase = new ReaderSQL(context, Database.ALL_PLAY_LISTS.DATABASE_NAME, null, 1);
        mRelationMusic = new RelationMusic(context);
        mDatabase.queryData(Database.ALL_PLAY_LISTS.CREATE_TABLE);
        mDatabase.close();
    }

    public AllPlayList closeDatabase() {

        mDatabase.close();

        return this;
    }

    public boolean isSelect(Cursor cursor){
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

    public void addPlayList(String mediaID) {
        String SQL_INSERT =
                "INSERT INTO "+ Database.ALL_PLAY_LISTS.TABLE_NAME
                        +" VALUES(null"  + ","+
                        "'" + mediaID +"'" + ")";

        mDatabase.queryData(SQL_INSERT);
        closeDatabase();
    }

    public void deletePlayList(int id) {
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.ALL_PLAY_LISTS.TABLE_NAME+" WHERE id= '"+id+ "' ";
        mDatabase.queryData(SQL_DELETE);
        closeDatabase();
    }

    public boolean deletePlayList(String namePlayList) {
        String SQL_DELETE =  "DELETE FROM " +Database.ALL_PLAY_LISTS.TABLE_NAME
                + " WHERE "+Database.ALL_PLAY_LISTS.NAME_PLAY_LIST+ " = "+"'"+namePlayList+"'";

        /*String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.ALL_PLAY_LISTS.TABLE_NAME+" WHERE " +
                        "name_play_list = "+name_play_list+ "";*/
        if (searchPlayList(namePlayList) && getSize() > 0){
            mDatabase.queryData(SQL_DELETE);
            mRelationMusic.deletePlayList(namePlayList);
            closeDatabase();
            return true;
        }else {

            return false;
        }
    }

    public void deleteAllPlayList() {
        try {
            mDatabase.queryData(Database.SONGS_OF_PLAY_LIST.DELETE);

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
    }

    public void updatePlayList(String main,String change){
        String SQL_UPDATE =
                "UPDATE "+
                        Database.ALL_PLAY_LISTS.TABLE_NAME+ " SET " +
                        Database.ALL_PLAY_LISTS.NAME_PLAY_LIST + "= '"+ change +"'" +
                        " WHERE "+ Database.ALL_PLAY_LISTS.NAME_PLAY_LIST + "= '"+ main+ "' ";
        mDatabase.queryData(SQL_UPDATE);
        closeDatabase();
    }

    public boolean searchPlayList(String namePlayList){
        Cursor playListData = mDatabase.getData(Database.ALL_PLAY_LISTS.QUERY);
        try {
            if (isSelect(playListData)) {
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
            MediaManager.getInstance().setContext(mContext);
            MediaManager.getInstance().addPlayListFirst();
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
            if (isSelect(query)) {
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
            if (isSelect(cursor)) {
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

    public HashMap<String, String> getPlayList(String namePlayList) {
        Cursor playListData = mDatabase.getData(Database.ALL_PLAY_LISTS.QUERY);
        String name = "";
        try {
            if (isSelect(playListData)) {
                do {
                    if (playListData.getString(1).equals (namePlayList)) {
                        name = playListData.getString(1);
                    }
                } while (!playListData.isAfterLast());

            }
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.VALUE.NAME_PLAYLIST, name);
            return hashMap;
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }

        return null;
    }
}
