package com.zhangyuhuishou.zyhs.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.zhangyuhuishou.zyhs.model.AdModel;
import com.zhangyuhuishou.zyhs.model.ClearDismissModel;
import com.zhangyuhuishou.zyhs.model.ClearMissModel;
import com.zhangyuhuishou.zyhs.model.InspectionModel;
import com.zhangyuhuishou.zyhs.model.OrderMissModel;
import com.zhangyuhuishou.zyhs.model.PointModel;
import com.zhangyuhuishou.zyhs.model.TerminalModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBManager {


    private final String TAG = DBManager.class.getSimpleName();

    private final String COLUMN_ID = "id";
    private final String COLUMN_ID_D = "ID_D";
    private final String COLUMN_RESP_STA = "Resp_sta";
    private final String COLUMN_WEIGHT_STA = "Weight_sta";
    private final String COLUMN_REC_BUCKET_STA = "Rec_Bucket_sta";
    private final String COLUMN_REC_GATE_STA = "Rec_Gate_sta";
    private final String COLUMN_MAIN_DOOR_STA = "Main_Door_sta";
    private final String COLUMN_LED_BOX_STA = "LED_Box_sta";
    private final String COLUMN_FAN_STA = "FAN_sta";
    private final String COLUMN_EQU_FAULT_STA = "equ_fault_sta";
    private final String COLUMN_VERSION = "version";
    private final String COLUMN_DEGERM_STA = "DEGERM_sta";
    private final String COLUMN_BUCKET_STA = "BUCKET_sta";
    private final String TABLE_NAME = "zyhs";
    private final String TABLE_NAME_DEV = "dev";
    private final String TABLE_NAME_AD = "ad";
    private static SQLiteDatabase db;

    //计数器
    private static int mCount;

    private MyDatabaseHelper helper;

    private DBManager(Context context) {
        helper = MyDatabaseHelper.getInstance(context);

    }

    private volatile static DBManager ourInstance = null;

    public static DBManager getInstance(Context context) {
        if (ourInstance == null) {
            synchronized (DBManager.class) {
                if (ourInstance == null) {
                    ourInstance = new DBManager(context);
                }
            }
        }
        return ourInstance;
    }

    /**
     * 打开SQLiteDatabase 对象
     */
    public synchronized SQLiteDatabase openDb() {
        if (mCount == 0) {
            db = helper.getWritableDatabase();
        }
        mCount++;
        return db;
    }

    /**
     * 需要关闭的SQLiteDatabase对象
     */
    public synchronized void closeDb(SQLiteDatabase database) {
        mCount--;
        if (mCount == 0) {

            database.close();
        }
    }

    // 插入数据
    public void insert(String ID_D, int RESP, int WEIGHT, int REC_BUCKET, int REC_GATE, int MAIN_DOOR, int LED_BOX, int FAN, int DEGERM, int BUCKET) {
        SQLiteDatabase db = null;
        try {
            //            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ID_D, ID_D);
            contentValues.put(COLUMN_RESP_STA, RESP);
            contentValues.put(COLUMN_WEIGHT_STA, WEIGHT);
            contentValues.put(COLUMN_REC_BUCKET_STA, REC_BUCKET);
            contentValues.put(COLUMN_REC_GATE_STA, REC_GATE);
            contentValues.put(COLUMN_MAIN_DOOR_STA, MAIN_DOOR);
            contentValues.put(COLUMN_LED_BOX_STA, LED_BOX);
            contentValues.put(COLUMN_FAN_STA, FAN);
            contentValues.put(COLUMN_DEGERM_STA, DEGERM);
            contentValues.put(COLUMN_BUCKET_STA, BUCKET);
            contentValues.put(COLUMN_VERSION, "1");// 默认版本号
            db.insertOrThrow(TABLE_NAME, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
    }

    // 获取所有数据
    public List<BucketStatusModel> getAllData() {
        List<BucketStatusModel> list = new ArrayList<>();
//        SQLiteDatabase db = helper.getReadableDatabase();
        SQLiteDatabase db = openDb();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "getAllData cursor is null or count <=0");
                return list;
            }
            while (cursor.moveToNext()) {
                BucketStatusModel model = new BucketStatusModel();
                model.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                model.setCOLUMN_ID(cursor.getString(cursor.getColumnIndex(COLUMN_ID_D)));
                model.setCOLUMN_RESP_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_RESP_STA)));
                model.setCOLUMN_WEIGHT_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_WEIGHT_STA)));
                model.setCOLUMN_REC_BUCKET_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_REC_BUCKET_STA)));
                model.setCOLUMN_REC_GATE_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_REC_GATE_STA)));
                model.setCOLUMN_MAIN_DOOR_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_MAIN_DOOR_STA)));
                model.setCOLUMN_LED_BOX_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_LED_BOX_STA)));
                model.setCOLUMN_FAN_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_FAN_STA)));
                model.setCOLUMN_DEGERM_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_DEGERM_STA)));
                model.setCOLUMN_BUCKET_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_BUCKET_STA)));
                model.setVersion(cursor.getString(cursor.getColumnIndex(COLUMN_VERSION)));
                list.add(model);
            }
            Log.d(TAG, "getAllData:" + list.size() + ",list:" + list);
        } catch (Exception e) {
            System.out.println("checkColumnExists1:" + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
//                db.close();
                closeDb(db);
            }
        }
        return list;
    }

    // 获取所有数据
    public List<BucketStatusModel> getAllDataExceptGlass() {
        List<BucketStatusModel> list = new ArrayList<>();
//        SQLiteDatabase db = helper.getReadableDatabase();
        SQLiteDatabase db = openDb();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "getAllData cursor is null or count <=0");
                return list;
            }
            while (cursor.moveToNext()) {
                BucketStatusModel model = new BucketStatusModel();
                model.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                model.setCOLUMN_ID(cursor.getString(cursor.getColumnIndex(COLUMN_ID_D)));
                model.setCOLUMN_RESP_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_RESP_STA)));
                model.setCOLUMN_WEIGHT_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_WEIGHT_STA)));
                model.setCOLUMN_REC_BUCKET_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_REC_BUCKET_STA)));
                model.setCOLUMN_REC_GATE_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_REC_GATE_STA)));
                model.setCOLUMN_MAIN_DOOR_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_MAIN_DOOR_STA)));
                model.setCOLUMN_LED_BOX_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_LED_BOX_STA)));
                model.setCOLUMN_FAN_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_FAN_STA)));
                model.setCOLUMN_DEGERM_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_DEGERM_STA)));
                model.setCOLUMN_BUCKET_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_BUCKET_STA)));
                model.setVersion(cursor.getString(cursor.getColumnIndex(COLUMN_VERSION)));

                if (!"02".equals(model.getCOLUMN_ID())) {

                    list.add(model);
                }

            }
            Log.d(TAG, "getAllDataExceptGlass:" + list.size() + ",list:" + list);
        } catch (Exception e) {
            System.out.println("checkColumnExists1:" + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
//                db.close();
                closeDb(db);
            }
        }
        return list;
    }


    // 删除数据
    public int deleteById(String ID_D) {
        int count = 0;
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            String whereClause = COLUMN_ID_D + " = ?";
            String[] whereArgs = new String[]{ID_D + ""};
            count = db.delete(TABLE_NAME, whereClause, whereArgs);
            db.setTransactionSuccessful();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
        return count;
    }

    // 更新数据（回收桶状态）
    public void updateRecBucket(String ID_D, int REC_BUCKET) {
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ID_D, ID_D);
            contentValues.put(COLUMN_REC_BUCKET_STA, REC_BUCKET);
            String whereClause = COLUMN_ID_D + " = ?";
            String[] whereArgs = new String[]{ID_D};
            db.update(TABLE_NAME, contentValues, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
    }

    // 更新数据（设备故障）
    public void update_species_fault(String ID_D, int species_fault, int Rec_Gate_sta) {
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_EQU_FAULT_STA, species_fault);
            contentValues.put(COLUMN_REC_GATE_STA, Rec_Gate_sta);
            String whereClause = COLUMN_ID_D + " = ?";
            String[] whereArgs = new String[]{ID_D};
            db.update(TABLE_NAME, contentValues, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
    }

    // 更新数据（回收桶版本）
    public void update_bucket_version(String ID_D, String version) {
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_VERSION, version);
            String whereClause = COLUMN_ID_D + " = ?";
            String[] whereArgs = new String[]{ID_D};
            db.update(TABLE_NAME, contentValues, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
    }

    // 查询某条数据是否存在
    public boolean queryIsExist(String ID_D) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            cursor = db.rawQuery("SELECT * FROM zyhs where ID_D ='" + ID_D + "'", null);
            cursor.moveToFirst();
            return cursor.getCount() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
//                db.close();
                closeDb(db);
            }
        }
    }

    // 获取某条数据
    public BucketStatusModel getModel(String ID_D) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
//            db = helper.getWritableDatabase();

            db = openDb();
            cursor = db.rawQuery("SELECT * FROM zyhs where ID_D ='" + ID_D + "'", null);
            cursor.moveToFirst();
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "getAllData cursor is null or count <=0");
                return null;
            }
            BucketStatusModel model;
            model = new BucketStatusModel();
            model.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            model.setCOLUMN_ID(cursor.getString(cursor.getColumnIndex(COLUMN_ID_D)));
            model.setCOLUMN_RESP_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_RESP_STA)));
            model.setCOLUMN_WEIGHT_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_WEIGHT_STA)));
            model.setCOLUMN_REC_BUCKET_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_REC_BUCKET_STA)));
            model.setCOLUMN_REC_GATE_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_REC_GATE_STA)));
            model.setCOLUMN_MAIN_DOOR_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_MAIN_DOOR_STA)));
            model.setCOLUMN_LED_BOX_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_LED_BOX_STA)));
            model.setCOLUMN_FAN_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_FAN_STA)));
            model.setCOLUMN_DEGERM_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_DEGERM_STA)));
            model.setCOLUMN_BUCKET_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_BUCKET_STA)));
            model.setCOLUMN_EQU_FAULT_STA(cursor.getInt(cursor.getColumnIndex(COLUMN_EQU_FAULT_STA)));
            model.setVersion(cursor.getString(cursor.getColumnIndex(COLUMN_VERSION)));
            return model;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
//                db.close();
                closeDb(db);
            }
        }
    }

    // 清空表中的数据但不删除表
    public void clearData() {
        int count = 0;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            db.beginTransaction();
            db.execSQL("DELETE FROM " + TABLE_NAME);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    // 丢失清理信息开始（改版）
    public List<ClearDismissModel> getDisClearInfo() {
        List<ClearDismissModel> list = new ArrayList<>();
        SQLiteDatabase db = openDb();
        Cursor cursor = null;
        try {
            cursor = db.query("disClearInfo", null, null, null, null, null, null);
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "getAllData cursor is null or count <=0");
                return list;
            }
            while (cursor.moveToNext()) {
                ClearDismissModel missModel = new ClearDismissModel();
                missModel.setId(cursor.getInt(cursor.getColumnIndex("id")));
                missModel.setRangeId(cursor.getString(cursor.getColumnIndex("rangeId")));
                missModel.setOprSysUserId(cursor.getString(cursor.getColumnIndex("oprSysUserId")));
                missModel.setEstimatePlasticBottleCount(cursor.getInt(cursor.getColumnIndex("estimatePlasticBottleCount")));
                missModel.setActualPlasticBottleCount(cursor.getInt(cursor.getColumnIndex("actualPlasticBottleCount")));
                missModel.setEstimateGlassBottleCount(cursor.getInt(cursor.getColumnIndex("estimateGlassBottleCount")));
                missModel.setActualGlassBottleCount(cursor.getInt(cursor.getColumnIndex("actualGlassBottleCount")));
                missModel.setEstimateCansCount(cursor.getInt(cursor.getColumnIndex("estimateCansCount")));
                missModel.setActualCansCount(cursor.getInt(cursor.getColumnIndex("actualCansCount")));
                missModel.setEstimatePaperCount(cursor.getInt(cursor.getColumnIndex("estimatePaperCount")));
                missModel.setActualPaperCount(cursor.getInt(cursor.getColumnIndex("actualPaperCount")));
                missModel.setEstimateSpinCount(cursor.getInt(cursor.getColumnIndex("estimateSpinCount")));
                missModel.setActualSpinCount(cursor.getInt(cursor.getColumnIndex("actualSpinCount")));
                missModel.setEstimatePlasticCount(cursor.getInt(cursor.getColumnIndex("estimatePlasticCount")));
                missModel.setActualPlasticCount(cursor.getInt(cursor.getColumnIndex("actualPlasticCount")));
                missModel.setEstimateMetalCount(cursor.getInt(cursor.getColumnIndex("estimateMetalCount")));
                missModel.setActualMetalCount(cursor.getInt(cursor.getColumnIndex("actualMetalCount")));
                list.add(missModel);
            }
            Log.d(TAG, "getAllData:" + list.size() + ",list:" + list);
        } catch (Exception e) {
            System.out.println("checkColumnExists1:" + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
                closeDb(db);
            }
        }
        return list;
    }

    // 插入数据（丢失清理信息）
    public void insertClearInfo(String oprSysUserId, String rangeId, int estimatePlasticBottleCount, int actualPlasticBottleCount,
                                int estimateGlassBottleCount, int actualGlassBottleCount, int estimateCansCount,
                                int actualCansCount, int estimatePaperCount, int actualPaperCount,
                                int estimateSpinCount, int actualSpinCount, int estimatePlasticCount,
                                int actualPlasticCount, int estimateMetalCount, int actualMetalCount) {
        SQLiteDatabase db = null;
        try {
            db = openDb();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("oprSysUserId", oprSysUserId);
            contentValues.put("rangeId", rangeId);
            contentValues.put("estimatePlasticBottleCount", estimatePlasticBottleCount);
            contentValues.put("actualPlasticBottleCount", actualPlasticBottleCount);
            contentValues.put("estimateGlassBottleCount", estimateGlassBottleCount);
            contentValues.put("actualGlassBottleCount", actualGlassBottleCount);
            contentValues.put("estimateCansCount", estimateCansCount);
            contentValues.put("actualCansCount", actualCansCount);
            contentValues.put("estimatePaperCount", estimatePaperCount);
            contentValues.put("actualPaperCount", actualPaperCount);
            contentValues.put("estimateSpinCount", estimateSpinCount);
            contentValues.put("actualSpinCount", actualSpinCount);
            contentValues.put("estimatePlasticCount", estimatePlasticCount);
            contentValues.put("actualPlasticCount", actualPlasticCount);
            contentValues.put("estimateMetalCount", estimateMetalCount);
            contentValues.put("actualMetalCount", actualMetalCount);
            db.insertOrThrow("disClearInfo", null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                closeDb(db);
            }
        }
    }

    // 删除数据（丢失的清理信息）
    public int deleteClearDataById(int id) {
        int count = 0;
        SQLiteDatabase db = null;
        try {
            db = openDb();
            db.beginTransaction();
            String whereClause = "id" + " = ?";
            String[] whereArgs = new String[]{id + ""};
            count = db.delete("disClearInfo", whereClause, whereArgs);
            db.setTransactionSuccessful();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                closeDb(db);
            }
        }
        return count;
    }

    // 丢失清理信息结束（改版）


    //插入巡检单数据
    public void insertInspectionInfo(String rangeId, String token, String remark, String report) {
        SQLiteDatabase db = null;
        try {
            db = openDb();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("rangeId", rangeId);
            contentValues.put("token", token);
            contentValues.put("remark", remark);
            contentValues.put("report", report);
            db.insertOrThrow("disInspection", null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                closeDb(db);
            }
        }
    }


    // 删除巡检单信息
    public int deleteInspectionById(int id) {
        int count = 0;
        SQLiteDatabase db = null;
        try {
            db = openDb();
            db.beginTransaction();
            String whereClause = "id" + " = ?";
            String[] whereArgs = new String[]{id + ""};
            count = db.delete("disInspection", whereClause, whereArgs);
            db.setTransactionSuccessful();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                closeDb(db);
            }
        }
        return count;
    }

    // 获取巡检单信息
    public List<InspectionModel> getDisInspectionInfo() {
        List<InspectionModel> list = new ArrayList<>();
        SQLiteDatabase db = openDb();
        Cursor cursor = null;
        try {
            cursor = db.query("disInspection", null, null, null, null, null, null);
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "getAllData cursor is null or count <=0");
                return list;
            }
            while (cursor.moveToNext()) {
                InspectionModel inspectionModel = new InspectionModel();
                inspectionModel.setId(cursor.getInt(cursor.getColumnIndex("id")));
                inspectionModel.setRangeId(cursor.getString(cursor.getColumnIndex("rangeId")));
                inspectionModel.setToken(cursor.getString(cursor.getColumnIndex("token")));
                inspectionModel.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
                inspectionModel.setReport(cursor.getString(cursor.getColumnIndex("report")));
                list.add(inspectionModel);
            }
            Log.d(TAG, "getAllData:" + list.size() + ",list:" + list);
        } catch (Exception e) {
            System.out.println("checkColumnExists1:" + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
                closeDb(db);
            }
        }
        return list;
    }

    // 丢失清理信息开始
    // 获取所有数据（丢失的订单数据）
    public List<ClearMissModel> getDisClearData() {
        List<ClearMissModel> list = new ArrayList<>();
//        SQLiteDatabase db = helper.getReadableDatabase();
        SQLiteDatabase db = openDb();
        Cursor cursor = null;
        try {
            cursor = db.query("disclear", null, null, null, null, null, null);
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "getAllData cursor is null or count <=0");
                return list;
            }
            while (cursor.moveToNext()) {
                ClearMissModel missModel = new ClearMissModel();
                missModel.setId(cursor.getInt(cursor.getColumnIndex("id")));
                missModel.setRangeId(cursor.getString(cursor.getColumnIndex("rangeId")));
                missModel.setTerminalId(cursor.getString(cursor.getColumnIndex("terminalId")));
                missModel.setRangeLogType(cursor.getString(cursor.getColumnIndex("rangeLogType")));
                missModel.setContent(cursor.getString(cursor.getColumnIndex("content")));
                missModel.setOprSysUserId(cursor.getString(cursor.getColumnIndex("oprSysUserId")));
                list.add(missModel);
            }
            Log.d(TAG, "getAllData:" + list.size() + ",list:" + list);
        } catch (Exception e) {
            System.out.println("checkColumnExists1:" + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
//                db.close();
                closeDb(db);
            }
        }
        return list;
    }

    // 删除数据（丢失的清理信息）
    public int deleteClearInfoById(int id) {
        int count = 0;
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            String whereClause = "id" + " = ?";
            String[] whereArgs = new String[]{id + ""};
            count = db.delete("disclear", whereClause, whereArgs);
            db.setTransactionSuccessful();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
        return count;
    }

    // 插入数据（丢失清理信息）
    public void insertClearData(String rangeId, String terminalId, String rangeLogType, String content, String oprSysUserId) {
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("rangeId", rangeId);
            contentValues.put("terminalId", terminalId);
            contentValues.put("rangeLogType", rangeLogType);
            contentValues.put("content", content);
            contentValues.put("oprSysUserId", oprSysUserId);
            db.insertOrThrow("disclear", null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
    }

    // 丢失清理信息结束

    // 获取所有数据（丢失的订单数据）
    public List<OrderMissModel> getDisOrderData() {
        List<OrderMissModel> list = new ArrayList<>();
//        SQLiteDatabase db = helper.getReadableDatabase();
        SQLiteDatabase db = openDb();

        Cursor cursor = null;
        try {
            cursor = db.query("disorder", null, null, null, null, null, null);
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "getAllData cursor is null or count <=0");
                return list;
            }
            while (cursor.moveToNext()) {
                OrderMissModel missModel = new OrderMissModel();
                missModel.setToken(cursor.getString(cursor.getColumnIndex("token")));
                missModel.setData(cursor.getString(cursor.getColumnIndex("data")));
                missModel.setId(cursor.getInt(cursor.getColumnIndex("id")));
                list.add(missModel);
            }
            Log.d(TAG, "getAllData:" + list.size() + ",list:" + list);
        } catch (Exception e) {
            System.out.println("checkColumnExists1:" + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
//                db.close();
                closeDb(db);
            }
        }
        return list;
    }

    // 删除数据
    public int deleteByTokenAndData(int id) {
        int count = 0;
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            String whereClause = "id" + " = ?";
            String[] whereArgs = new String[]{id + ""};
            count = db.delete("disorder", whereClause, whereArgs);
            db.setTransactionSuccessful();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
        return count;
    }

    // 插入数据（丢失订单）
    public void insertOrderData(String token, String data) {
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("token", token);
            contentValues.put("data", data);
            db.insertOrThrow("disorder", null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
    }

    // 广告相关（开始）
    // 插入数据
    public void insertAd(AdModel.DataBean bean) {
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("adId", bean.getAdId());
            contentValues.put("adDetailId", bean.getAdDetailId());
            contentValues.put("filePath", bean.getFilePath());
            contentValues.put("fileUrl", bean.getFileUrl());
            contentValues.put("md5Name", bean.getMd5Name());
            contentValues.put("duration", bean.getDuration() * 1000);
            db.insertOrThrow("ad", null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
    }

    // 获取所有数据
    public List<AdModel.DataBean> getAdList() {
        List<AdModel.DataBean> list = new ArrayList<>();
//        SQLiteDatabase db = helper.getReadableDatabase();
        SQLiteDatabase db = openDb();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME_AD, null, null, null, null, null, null);
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "getAllData cursor is null or count <=0");
                return list;
            }
            while (cursor.moveToNext()) {
                AdModel.DataBean bean = new AdModel.DataBean();
                bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                bean.setAdId(cursor.getString(cursor.getColumnIndex("adId")));
                bean.setAdDetailId(cursor.getString(cursor.getColumnIndex("adDetailId")));
                bean.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
                bean.setFileUrl(cursor.getString(cursor.getColumnIndex("fileUrl")));
                bean.setMd5Name(cursor.getString(cursor.getColumnIndex("md5Name")));
                bean.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
                list.add(bean);
            }
            Log.d(TAG, "getAllData:" + list.size() + ",list:" + list);
        } catch (Exception e) {
            System.out.println("checkColumnExists1:" + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
//                db.close();
                closeDb(db);
            }
        }
        return list;
    }

    // 清空表中的数据但不删除表
    public void clearAdList() {
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();

            db.beginTransaction();
            if (helper.tableIsExist(db, TABLE_NAME_AD)) {
                db.execSQL("DELETE FROM " + TABLE_NAME_AD);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
    }

    // 判断某条广告是否存在
    public boolean queryIsAdExist(String md5Name) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();

            cursor = db.rawQuery("SELECT * FROM ad where md5Name ='" + md5Name + "'", null);
            cursor.moveToFirst();
            return cursor.getCount() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
//                db.close();
                closeDb(db);

            }
        }
    }
    // 广告相关（结束）

    // 设备相关
    // 插入数据
    public void insertDev(PointModel.DataBean.TerminalListBean bean) {
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("weight", 0);
            contentValues.put("terminalId", bean.getTerminalId());
            contentValues.put("terminalTypeId", bean.getTerminalTypeId());
            contentValues.put("terminalTypeName", bean.getTerminalTypeName());
            contentValues.put("terminalNum", bean.getTerminalNum());
            db.insertOrThrow("dev", null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
    }

    // 获取所有数据
    public List<TerminalModel> getDevList() {
        List<TerminalModel> list = new ArrayList<>();
//        SQLiteDatabase db = helper.getReadableDatabase();
        SQLiteDatabase db = openDb();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME_DEV, null, null, null, null, null, null);
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "getAllData cursor is null or count <=0");
                return list;
            }
            while (cursor.moveToNext()) {
                TerminalModel terminalModel = new TerminalModel();
                terminalModel.setId(cursor.getInt(cursor.getColumnIndex("id")));
                terminalModel.setTerminalId(cursor.getString(cursor.getColumnIndex("terminalId")));
                terminalModel.setTerminalTypeId(cursor.getString(cursor.getColumnIndex("terminalTypeId")));
                terminalModel.setTerminalTypeName(cursor.getString(cursor.getColumnIndex("terminalTypeName")));
                terminalModel.setWeight(cursor.getInt(cursor.getColumnIndex("weight")));
                terminalModel.setID_D(cursor.getString(cursor.getColumnIndex("ID_D")));
                terminalModel.setTerminalNum(cursor.getString(cursor.getColumnIndex("terminalNum")));
                terminalModel.setCountException(cursor.getInt(cursor.getColumnIndex("countException")));
                list.add(terminalModel);
            }
            Log.d(TAG, "getAllData:" + list.size() + ",list:" + list);
        } catch (Exception e) {
            System.out.println("checkColumnExists1:" + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
//                db.close();
                closeDb(db);
            }
        }

        return list;

    }

    // 根据设备类型获取设备数据
    public List<TerminalModel> getDevListByType(String terminalTypeId) {
        List<TerminalModel> list = new ArrayList<>();
        Cursor cursor = null;
//        SQLiteDatabase db = helper.getReadableDatabase();
        SQLiteDatabase db = openDb();
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_DEV + " where terminalTypeId = '" + terminalTypeId + "'", null);
            if (cursor == null || cursor.getCount() <= 0) {
                Log.i(TAG, "getAllData cursor is null or count <=0");
                return list;
            }
            while (cursor.moveToNext()) {
                TerminalModel terminalModel = new TerminalModel();
                terminalModel.setId(cursor.getInt(cursor.getColumnIndex("id")));
                terminalModel.setID_D(cursor.getString(cursor.getColumnIndex("ID_D")));
                terminalModel.setTerminalId(cursor.getString(cursor.getColumnIndex("terminalId")));
                terminalModel.setTerminalTypeId(cursor.getString(cursor.getColumnIndex("terminalTypeId")));
                terminalModel.setTerminalTypeName(cursor.getString(cursor.getColumnIndex("terminalTypeName")));
                terminalModel.setWeight(cursor.getInt(cursor.getColumnIndex("weight")));
                terminalModel.setTerminalNum(cursor.getString(cursor.getColumnIndex("terminalNum")));
                terminalModel.setCountException(cursor.getInt(cursor.getColumnIndex("countException")));
                list.add(terminalModel);
            }
            Log.d(TAG, "getAllData:" + list.size() + ",list:" + list);
        } catch (Exception e) {
            System.out.println("checkColumnExists1:" + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
//                db.close();
                closeDb(db);
            }
        }
        return list;
    }

    // 清空表中的数据但不删除表
    public void clearDevList() {
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            db.execSQL("DELETE FROM " + TABLE_NAME_DEV);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
    }

    // 更新设备数据（某条）
    public void updateDev(int id, int weight) {
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", id);
            contentValues.put("weight", weight);
            String whereClause = "id" + " = ?";
            String[] whereArgs = new String[]{id + ""};
            db.update(TABLE_NAME_DEV, contentValues, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
    }

    // 更新设备数据（计数红外遮挡异常 0 正常  1 满了）
    public void updateCountException(String terminalId, int countException) {
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("terminalId", terminalId);
            contentValues.put("countException", countException);
            String whereClause = "terminalId" + " = ?";
            String[] whereArgs = new String[]{terminalId + ""};
            db.update(TABLE_NAME_DEV, contentValues, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
    }

    // 更新设备数据（某条）
    public void updateDev(int id, String ID_D) {
        SQLiteDatabase db = null;
        try {
//            db = helper.getWritableDatabase();
            db = openDb();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", id);
            contentValues.put(COLUMN_ID_D, ID_D);
            String whereClause = "id" + " = ?";
            String[] whereArgs = new String[]{id + ""};
            db.update(TABLE_NAME_DEV, contentValues, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
//                db.close();
                closeDb(db);
            }
        }
    }
    // 设备相关

    /**
     * 方法1：检查某表列是否存在
     *
     * @param db
     * @param tableName  表名
     * @param columnName 列名
     * @return
     */
    private boolean checkColumnExist1(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            //查询一行
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
            result = cursor != null && cursor.getColumnIndex(columnName) != -1;
        } catch (Exception e) {
            System.out.println("checkColumnExists1:" + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return result;
    }


    /**
     * 方法2：检查表中某列是否存在
     *
     * @param db
     * @param tableName  表名
     * @param columnName 列名
     * @return
     */
    private boolean checkColumnExists2(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false;
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from sqlite_master where name = ? and sql like ?"
                    , new String[]{tableName, "%" + columnName + "%"});
            result = null != cursor && cursor.moveToFirst();
        } catch (Exception e) {
            System.out.println("checkColumnExists1:" + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return result;
    }

}
