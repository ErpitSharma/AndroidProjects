package com.neon.arpit.starplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by arpit on 02-02-2017.
 */
public class DatabaseAdapter {

    public static final String DATABASE_NAME = "StarDB";
    public static final int DATABASE_VERSION= 1;

    //fields names
    public static final String SongTitle= "Title";
    public static final String Artist= "Artist";
    public static final String Album= "Album";
    public static final String HashValue= "HashCode";
    public static final String Row_ID= "_id";
    public static final String AlbumArt= "Album_Art";
    public static final String Path= "SongPath";
    public static final String DBCompleted= "Flag";
    public static final String SortingBy= "Sorter";
    public static final String Order= "Sort_Order";
    public static final String Shuffle="Shuffle_Flag";
    public static final String Repeat="Repeat_Flag";
    public static final String PlaylistName="playlist_name";
    public static final String PlaylistCode="playlist_Songs";
    public static final String vidFlag ="vidFlag";

    //Table Names
    public static final String MAIN_TABLE= "Table_list";
    public static final String STATUS_TABLE= "flag_table";
    public static final String ARTIST_TABLE= "artist_table";
    public static final String ALBUM_TABLE= "album_table";
    public static final String PLAYLIST_TABLE="playlist_table";
    public static final String VIDEO_TABLE="video_table";


    //Colmun arrays
    String[] ALL_COLUMNS =new String[] {Row_ID,Path,SongTitle,Artist,Album,HashValue,AlbumArt, vidFlag};
    String[] ART_COLUMNS=new String[] {Row_ID,Artist};
    String[] ALB_COLUMNS=new String[] {Row_ID,Album};
    String[] PLA_COLUMNS=new String[] {Row_ID,PlaylistName,PlaylistCode};



    private static final String CREATE_TABLE=
            "CREATE TABLE "+ MAIN_TABLE
                    + "(" + Row_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Path + " TEXT, " + SongTitle + " TEXT, " + Artist + " TEXT, " + Album + " TEXT, " +
                    HashValue + " TEXT, " +AlbumArt +" VARBINARY, "+ vidFlag +" INTEGER " + ");";

    private static final String CREATE_META =
            "CREATE TABLE "+ STATUS_TABLE +"("+Row_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    DBCompleted+" INTEGER, "+ SortingBy +" TEXT, " +Order +" TEXT, "+ Shuffle +" INTEGER, "
                    +Repeat+" INTEGER);";

    private static final String CREATE_VID =
            "CREATE TABLE "+ VIDEO_TABLE +"("+Row_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    Path +" TEXT);";

    private static final String DATABASE_CREATE_ARTIST =
            "CREATE TABLE " + ARTIST_TABLE
                    + " (" + Row_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Artist + " TEXT"
                    + ");";

    private static final String DATABASE_CREATE_ALBUM =
            "CREATE TABLE " + ALBUM_TABLE
                    + " (" + Row_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Album + " TEXT"
                    + ");";

    private static final String DATABASE_CREATE_PLAYLIST =
            "CREATE TABLE " + PLAYLIST_TABLE
                    + " (" + Row_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PlaylistName + " TEXT, "+PlaylistCode+ " TEXT"
                    + ");";


    private final Context context;
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase DB;

    public DatabaseAdapter(Context ctx) {

        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);

    }

    public DatabaseAdapter open()
    {
        DB=myDBHelper.getReadableDatabase();
        DB= myDBHelper.getWritableDatabase();
        return this;
    }

    public Cursor close()
    {
        myDBHelper.close();
        return null;
    }
    //--------------------------------------------------------------------
    //   Status Table functions

    public void InsertStatus(int status)
    {
        ContentValues initialValues=new ContentValues();
        initialValues.put(DBCompleted,status);

        Log.e("DataBase ", "status replaced with"+status);
        long effect= DB.update(STATUS_TABLE,initialValues, Row_ID+"= 1",null);

        if (effect==0) {
            Log.e("DataBase ", "status inserted "+status);
            DB.insert(STATUS_TABLE,null,initialValues);
        }

    }

    public void SaveSortingBy(String sorter, String order)
    {
        ContentValues initialValues=new ContentValues();
        initialValues.put(SortingBy,sorter);
        initialValues.put(Order,order);
        try {
            long effect=DB.update(STATUS_TABLE,initialValues, Row_ID+" =1",null);
            Log.e("DataBase", "Sorting songs by "+sorter+" "+order+" effect"+effect);
        } catch (Exception e) {
            Log.e("DataBase", "Added Sorting songs by "+sorter);
        }
    }

    public Boolean isDBDone()
    {
        open();
        Cursor cursor;
        cursor=DB.query(STATUS_TABLE, new String[]{DBCompleted},null,null,null,null,null);
        if (!cursor.moveToNext())
        {
            Log.e("DataBase ", "no data");
            close();
           return false;
        }
        cursor.moveToPosition(0);
        int status=cursor.getInt(cursor.getColumnIndex(DBCompleted));
        if (status==1) {
            Log.e("DataBase ", "data verified"+status);
            return true;
        }
        else
        {
            Log.e("DataBase ", "had error last time "+status);

            close();
            return false;
        }

    }

    public String getSortingBy()
    {
        Cursor cursor= DB.query(STATUS_TABLE, new String[]{SortingBy},null,null,null,null,null);
        cursor.moveToPosition(0);
        String alpha=cursor.getString(cursor.getColumnIndex(SortingBy));
        Log.e("Database","getting sortBy "+alpha);
        return alpha;
    }
    public String getSortOrder()
    {
        Cursor cursor= DB.query(STATUS_TABLE, new String[]{Order},null,null,null,null,null);
        cursor.moveToPosition(0);
        String alpha=cursor.getString(cursor.getColumnIndex(Order));
        Log.e("Database","getting sort order "+alpha);
        return alpha;
    }

    public void saveShuffleState (int flag)
    {
        ContentValues initialValues=new ContentValues();
        initialValues.put(Shuffle,flag);

        DB.update(STATUS_TABLE,initialValues, Row_ID+"= 1",null);
    }
    public int getShuffleState()
    {
        Cursor cursor= DB.query(STATUS_TABLE, new String[]{Shuffle},null,null,null,null,null);
        cursor.moveToPosition(0);
        int alpha=cursor.getInt(cursor.getColumnIndex(Shuffle));
        Log.e("Database","getting Shuffle State "+alpha);
        return alpha;
    }

    public void saveRepeatState (int flag)
    {
        ContentValues initialValues=new ContentValues();
        initialValues.put(Repeat,flag);

        DB.update(STATUS_TABLE,initialValues, Row_ID+"= 1",null);
    }
    public int getRepeatState()
    {
        Cursor cursor= DB.query(STATUS_TABLE, new String[]{Repeat},null,null,null,null,null);
        cursor.moveToPosition(0);
        int alpha=cursor.getInt(cursor.getColumnIndex(Repeat));
        Log.e("Database","getting Repeat State "+alpha);
        return alpha;
    }


//-------------------------------------------------------------------------------
//                       functions to access Main Table
    public long insertRow(String path, String title, String album, String artist, byte[] image, int flag)
    {
        ContentValues initialValues=new ContentValues();

        initialValues.put(Path,path);
        initialValues.put(SongTitle, title);
        initialValues.put(Album, album);
        initialValues.put(Artist,artist);
        initialValues.put(vidFlag,flag);
        String temp=title+""+album+""+artist;
        initialValues.put(HashValue, temp.hashCode());
        initialValues.put(AlbumArt, image);
        Log.e("DB","Title= "+title+" Artist= "+artist+" Album= "+temp.hashCode());
        return DB.insert(MAIN_TABLE, null, initialValues);
    }

    public boolean deleteRow(long rowId) {
        String where = Row_ID + "=" + rowId;
        Log.e("DB","deleting");
        return DB.delete(MAIN_TABLE, where, null) ==1;
    }

    public Cursor getSongs()
    {
        String where= vidFlag + "= 0";
        Cursor cursor=DB.query(MAIN_TABLE, ALL_COLUMNS,where,null,null,null,StaticData.SortingBy+" "+StaticData.order);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }
    public Cursor getSong(int code)
    {
        String where= Row_ID + " = "+ code;
        Cursor cursor=DB.query(MAIN_TABLE, ALL_COLUMNS,where,null,null,null,null);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }


    public Cursor getVideos()
    {
        String where= vidFlag + "= 1";
        Cursor cursor=DB.query(MAIN_TABLE, ALL_COLUMNS,where,null,null,null,StaticData.SortingBy+" "+StaticData.order);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }

    public Cursor getArtistSongs(int artistCode)
    {
        String where = Artist + "=" + artistCode;
        Cursor cursor=DB.query(MAIN_TABLE, ALL_COLUMNS,
                where,null,null,null,StaticData.SortingBy+" "+StaticData.order);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }

    public Cursor getAlbumSongs(int albCode)
    {
        String where = Album + "=" + albCode;
        Cursor cursor=DB.query(MAIN_TABLE, ALL_COLUMNS,
                where,null,null,null,StaticData.SortingBy+" "+StaticData.order);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }

    public Cursor searchSong(String text)
    {
        String where="Title LIKE '%"+text+"%' AND "+ vidFlag + " = 0";
        Cursor cursor=DB.query(MAIN_TABLE, ALL_COLUMNS,where,null,null,null,StaticData.SortingBy+" "+StaticData.order);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }



    public Cursor getPlaylistSongs(String art) {

        Cursor c = null;
        if (art!=null) {
            if (!art.equals("")) {
                c = DB.query(true, MAIN_TABLE, ALL_COLUMNS, art, null, null, null, null, null);
                if (c != null) {
                    c.moveToFirst();
                }
            }
        }
        return c;
    }
    public int IsPlaylistnameNew(String str)
    {
        int temp = getPlayCode(str);
        if (temp >= 0)
            return temp;
        return -1;
    }


//------------------------------------------------------------------------------
    // Functions to access Artist table

    public int getArtCode(String artist)
    {
        String where = Artist + "='" + artist+"'";
        Cursor c = 	DB.query(ARTIST_TABLE, ART_COLUMNS,
                where, null, null,null,null);
        try {
            c.moveToFirst();
            if (c.getCount()>0) {

                Log.e("Artist Database","Working");
                return c.getInt(0);
            }
        } catch (Exception e) {
            Log.e("Artist not found ",e.toString());
            return 0;
        }
        return 0;
    }

    public long insertArtist(String artist) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(Artist, artist);

        // Insert the data into the database.
        return DB.insert(ARTIST_TABLE, null, initialValues);
    }

    public Cursor getArtists()
    {
        Cursor cursor;
        cursor=DB.query(ARTIST_TABLE,ART_COLUMNS,null,null,null,null,(Artist+" "+StaticData.order));
        if (cursor != null) {
            try {
                cursor.moveToFirst();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }

    public String getArtistName(int code)
    {
        String artistName="unknown";
        String where= Row_ID + " = "+ code;
        Cursor cursor=DB.query(ARTIST_TABLE, ART_COLUMNS,where,null,null,null,null);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                artistName=cursor.getString(cursor.getColumnIndex(Artist));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return artistName;
    }

    public Cursor searchArtists(String text)
    {
        String where="Artist LIKE '%"+text+"%'";
        Cursor cursor=DB.query(ARTIST_TABLE, ART_COLUMNS,where,null,null,null,Artist);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }





//------------------------------------------------------------------------------
    // Functions to access Album table

    public int getAlbCode(String artist)
    {
        String where = Album + "='" + artist+"'";
        Cursor c = 	DB.query(ALBUM_TABLE, ALB_COLUMNS,
                where, null, null,null,null);       //where ke arg, group by, having, orderby
        try {
            c.moveToFirst();
            if (c.getCount()>0) {

                Log.e("Album Database","Working");
                return c.getInt(0);
            }
        } catch (Exception e) {
            Log.e("Album not found ",e.toString());
            return 0;
        }
        return 0;
    }

    public long insertAlbum(String album) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(Album, album);

        // Insert the data into the database.
        return DB.insert(ALBUM_TABLE, null, initialValues);
    }

    public Cursor getAlbums()
    {
        Cursor cursor;
        cursor=DB.query(ALBUM_TABLE, ALB_COLUMNS,null,null,null,null,null);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }


    public Cursor searchAlbums(String text)
    {
        String where="Album LIKE '%"+text+"%'";
        Cursor cursor=DB.query(ALBUM_TABLE, ALB_COLUMNS,where,null,null,null,Album);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }

    public String getAlbumName(int code)
    {
        String artistName="unknown";
        String where= Row_ID + " = "+ code;
        Cursor cursor=DB.query(ALBUM_TABLE, ALB_COLUMNS,where,null,null,null,null);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                artistName=cursor.getString(cursor.getColumnIndex(Album));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return artistName;
    }
//-------------------------------------------------------------------------------
    // Playlist tables code here

    public void insertPlaylist(String name,String hash)
    {
        ContentValues initialValues=new ContentValues();
        initialValues.put(PlaylistName, name );
        if (!hash.equals("")) {
            String s=HashValue+ "=";
            initialValues.put(PlaylistCode, s.concat(hash));
        }
        else
            initialValues.put(PlaylistCode,"");

        DB.insert(PLAYLIST_TABLE,null,initialValues);
    }

    public Cursor getPlaylists() {

        Cursor c = 	DB.query(true, PLAYLIST_TABLE, PLA_COLUMNS, null, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public boolean AddToPlaylist(String id, int task) {
        String where = Row_ID + "=" + task;
        ContentValues newValues = new ContentValues();
        Cursor c=getPlayRow(task);
        String s=c.getString(c.getColumnIndex(PlaylistCode));

        if (s.equals("")) {
            newValues.put(PlaylistCode, s.concat(HashValue+"="+id));
        }
        else
            newValues.put(PlaylistCode, s.concat(" OR "+HashValue+"="+id));

        // Insert it into the database.
        return DB.update(PLAYLIST_TABLE, newValues, where, null) != 0;
    }

    public Cursor getPlayRow(long rowId) {
        String where = Row_ID + "=" + rowId;
        Cursor c = 	DB.query(true, PLAYLIST_TABLE, PLA_COLUMNS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    public int getPlayCode(String playlist)
    {
        String where = PlaylistName + "='" + playlist+"'";
        Log.e(where,PLAYLIST_TABLE);
        Cursor c = 	DB.query(PLAYLIST_TABLE, PLA_COLUMNS,
                where, null, null,null,null,null);       //where ke arg, group by, having, orderby
        try {
            c.moveToFirst();
            if (c.getCount()>0)
            {

                Log.e("catch may abhi ni gya",c.getString(0).trim());
                return c.getInt(0);
            }
            if(c.getCount()==0)
            {
                return 0;
            }
        }
        catch (Exception e)
        {
            Log.e("ni chal sakta kyonki",e.toString());
            return -1;
        }
        return -1;
    }

//----------------------------------------------------------------------------------

    public void DeleteMainTable()
    {
        DB.execSQL("DROP TABLE IF EXISTS "+ MAIN_TABLE);
        DB.execSQL("DROP TABLE IF EXISTS "+ VIDEO_TABLE);
        DB.execSQL(CREATE_TABLE);
        DB.execSQL(CREATE_VID);
        DB.execSQL("DROP TABLE IF EXISTS "+ ARTIST_TABLE);
        DB.execSQL("DROP TABLE IF EXISTS "+ ALBUM_TABLE);
        DB.execSQL(DATABASE_CREATE_ARTIST);
        DB.execSQL(DATABASE_CREATE_ALBUM);
    }



    public class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION );

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try{
                db.execSQL(CREATE_TABLE);
                db.execSQL(CREATE_VID);
                db.execSQL(CREATE_META);
                db.execSQL(DATABASE_CREATE_ARTIST);
                db.execSQL(DATABASE_CREATE_ALBUM);
                db.execSQL(DATABASE_CREATE_PLAYLIST);
            }catch (Exception E)
            {
                Log.e("DataBase ", "Creation Error!"+E.toString());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS "+ CREATE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+ CREATE_VID);
            db.execSQL("DROP TABLE IF EXISTS "+ CREATE_META);
            db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_CREATE_ARTIST);
            db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_CREATE_ALBUM);
            db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_CREATE_PLAYLIST);
        }
    }
}