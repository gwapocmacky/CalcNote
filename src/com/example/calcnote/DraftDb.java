package com.example.calcnote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DraftDb {
    private final SQLiteOpenHelper helper;

    public static class Draft {
        public long id;
        public String name;
        public String content;
        public long updated;
        public Draft(long id, String name, String content, long updated) {
            this.id = id; this.name = name; this.content = content; this.updated = updated;
        }
    }

    public DraftDb(Context context) {
        helper = new SQLiteOpenHelper(context, "calcnote_drafts.db", null, 1) {
            @Override public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE drafts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, content TEXT, updated INTEGER)");
            }
            @Override public void onUpgrade(SQLiteDatabase db, int o, int n) {}
        };
    }

    public long createDraft(String name, String content) {
        SQLiteDatabase db = helper.getWritableDatabase();
        android.content.ContentValues cv = new android.content.ContentValues();
        cv.put("name", name);
        cv.put("content", content == null ? "" : content);
        cv.put("updated", System.currentTimeMillis());
        return db.insert("drafts", null, cv);
    }

    public void updateDraft(long id, String name, String content) {
        SQLiteDatabase db = helper.getWritableDatabase();
        android.content.ContentValues cv = new android.content.ContentValues();
        if (name != null) cv.put("name", name);
        cv.put("content", content == null ? "" : content);
        cv.put("updated", System.currentTimeMillis());
        db.update("drafts", cv, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteDraft(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("drafts", "id=?", new String[]{String.valueOf(id)});
    }

    public Draft getDraft(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id,name,content,updated FROM drafts WHERE id=?", new String[]{String.valueOf(id)});
        Draft d = null;
        if (c.moveToFirst()) {
            d = new Draft(c.getLong(0), c.getString(1), c.getString(2), c.getLong(3));
        }
        c.close();
        return d;
    }

    public List<Draft> listDrafts() {
        List<Draft> out = new ArrayList<Draft>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id,name,content,updated FROM drafts ORDER BY updated DESC", null);
        while (c.moveToNext()) {
            out.add(new Draft(c.getLong(0), c.getString(1), c.getString(2), c.getLong(3)));
        }
        c.close();
        return out;
    }
}
