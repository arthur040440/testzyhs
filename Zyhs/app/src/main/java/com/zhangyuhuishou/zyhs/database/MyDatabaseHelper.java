package com.zhangyuhuishou.zyhs.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;
    private String TAG = MyDatabaseHelper.class.getSimpleName();

    //创建local.db的数据库, 数据库中新建一张zyhs表, 其中有id(主键) SQL建表语句如下:
    /****
     *  integer: 表示整型;
     *  real: 表示浮点型;
     *  text: 表示文本型;
     *  blob:表示二进制类型
     *  primary key: 表示主键;
     *  autoincrement: 表示自增长
     */

    // 设备状态
    public static final String CREATE_ZYHS = "create table zyhs (" +
            "id integer primary key autoincrement, " +
            "ID_D text, " +
            "Resp_sta integer, " +
            "Weight_sta integer, " +
            "Rec_Bucket_sta integer, " +
            "Rec_Gate_sta integer, " +
            "Main_Door_sta integer, " +
            "LED_Box_sta integer, " +
            "FAN_sta integer, " +
            "DEGERM_sta integer, " +
            "equ_fault_sta integer, " +
            "BUCKET_sta integer, " +
            "version text)";

    // 订单表（存储意外订单）
    public static final String CREATE_ZYHS_ORDER = "create table disorder (" +
            "id integer primary key autoincrement, " +
            "token text, " +
            "data text)";

    // 设备表（存储设备列表）
    public static final String CREATE_ZYHS_DEV = "create table dev (" +
            "id integer primary key autoincrement, " +
            "ID_D text, " +
            "weight integer, " +
            "terminalId text, " +
            "terminalTypeId text, " +
            "terminalNum text, " +
            "countException integer, " +
            "terminalTypeName text)";

    // 广告信息
    public static final String CREATE_ZYHS_AD = "create table ad (" +
            "id integer primary key autoincrement, " +
            "adId text, " +
            "adDetailId text, " +
            "filePath text, " +
            "fileUrl text, " +
            "md5Name text, " +
            "duration integer)";

    // 清理数据（存储意外清理信息）
    public static final String CREATE_ZYHS_CLEAR = "create table disclear (" +
            "id integer primary key autoincrement, " +
            "rangeId text, " +
            "terminalId text, " +
            "rangeLogType text, " +
            "oprSysUserId text, " +
            "content text)";

    // 清理数据（改版-存储意外清理信息）
    public static final String CREATE_ZYHS_DIS_CLEAR = "create table disClearInfo (" +
            "id integer primary key autoincrement, " +
            "oprSysUserId text, " +
            "rangeId text, " +
            "estimatePlasticBottleCount integer, " +
            "actualPlasticBottleCount integer, " +
            "estimateGlassBottleCount integer, " +
            "actualGlassBottleCount integer, " +
            "estimateCansCount integer, " +
            "actualCansCount integer, " +
            "estimatePaperCount integer, " +
            "actualPaperCount integer, " +
            "estimateSpinCount integer, " +
            "actualSpinCount integer, " +
            "estimatePlasticCount integer, " +
            "actualPlasticCount integer, " +
            "estimateMetalCount integer, " +
            "actualMetalCount integer)";

    // 运维巡检数据
    public static final String CREATE_DIS_INSPECTION = "create table disInspection (" +
            "id integer primary key autoincrement, " +
            "rangeId text, " +
            "token text, " +
            "remark text, " +
            "report text)";

    private volatile static MyDatabaseHelper instance;

    public static MyDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (MyDatabaseHelper.class) {
                if (instance == null) {
                    instance = new MyDatabaseHelper(context);
                }
            }
        }
        return instance;
    }

    private MyDatabaseHelper(Context context) {
        super(context, "local.db", null, 9);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ZYHS);
        db.execSQL(CREATE_ZYHS_ORDER);
        db.execSQL(CREATE_ZYHS_DEV);
        db.execSQL(CREATE_ZYHS_AD);
        db.execSQL(CREATE_ZYHS_CLEAR);
        db.execSQL(CREATE_ZYHS_DIS_CLEAR);
        db.execSQL(CREATE_DIS_INSPECTION);
        Log.i(TAG, "创建数据库成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (!tableIsExist(db, "zyhs")) {
            db.execSQL(CREATE_ZYHS);
        }

        if (!tableIsExist(db, "dev")) {
            db.execSQL(CREATE_ZYHS_DEV);
        }

        if (!tableIsExist(db, "ad")) {
            db.execSQL(CREATE_ZYHS_AD);
        }

        if (!tableIsExist(db, "disclear")) {
            db.execSQL(CREATE_ZYHS_CLEAR);
        }

        if (!tableIsExist(db, "disClearInfo")) {
            db.execSQL(CREATE_ZYHS_DIS_CLEAR);
        }

        if (!tableIsExist(db, "disInspection")) {
            db.execSQL(CREATE_DIS_INSPECTION);
        }

        if (!checkColumnExists(db, "zyhs", "ID_D")) {
            String sql_zyhs = "Alter table zyhs add column ID_D" + " TEXT ";
            db.execSQL(sql_zyhs);
        }

        if (!checkColumnExists(db, "dev", "ID_D")) {
            String sql_add_ID_D = "Alter table dev add column ID_D" + " TEXT ";
            db.execSQL(sql_add_ID_D);
        }

        if (!checkColumnExists(db, "dev", "terminalNum")) {
            String sql_add_terminalNum = "Alter table dev add column terminalNum" + " TEXT ";
            db.execSQL(sql_add_terminalNum);
        }


        if (!checkColumnExists(db, "zyhs", "equ_fault_sta")) {
            String sql_zyhs_fault = "Alter table zyhs add column equ_fault_sta integer";
            db.execSQL(sql_zyhs_fault);
        }

        if (!checkColumnExists(db, "zyhs", "version")) {
            String sql_zyhs_version = "Alter table zyhs add column version text";
            db.execSQL(sql_zyhs_version);
        }

        if (!checkColumnExists(db, "dev", "countException")) {
            String sql_add_terminalNum = "Alter table dev add column countException integer ";
            db.execSQL(sql_add_terminalNum);
        }

    }

    /**
     * 检查表中某列是否存在
     *
     * @param db
     * @param tableName  表名
     * @param columnName 列名
     * @return
     */
    private boolean checkColumnExists(SQLiteDatabase db, String tableName
            , String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from sqlite_master where name = ? and sql like ?"
                    , new String[]{tableName, "%" + columnName + "%"});
            result = null != cursor && cursor.moveToFirst();
        } catch (Exception e) {
            Log.e(TAG, "checkColumnExists2..." + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 判断某张表是否存在
     *
     * @param db        数据库
     * @param tableName 表名
     * @return
     */
    public boolean tableIsExist(SQLiteDatabase db, String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }
}
