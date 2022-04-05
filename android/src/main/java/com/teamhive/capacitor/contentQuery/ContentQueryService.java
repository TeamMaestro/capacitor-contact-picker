package com.teamhive.capacitor.contentQuery;

import android.content.Context;
import android.database.Cursor;
import com.getcapacitor.JSObject;
import com.teamhive.capacitor.utils.Utils;
import com.teamhive.capacitor.utils.Visitable;
import com.teamhive.capacitor.utils.Visitor;

import java.io.Closeable;
import java.util.Map;

public class ContentQueryService {

    public static VisitableCursorWrapper query(Context context, ContentQuery query) {
        try {
            String[] projectionArray = Utils.getMapKeysAsArray(query.getProjection());
            Cursor cursor = context.getContentResolver().query(query.getUri(), projectionArray, query.getSelection(), query.getSelectionArgs(), query.getSortOrder(), query.getCancellationSignal());
            return new VisitableCursorWrapper(cursor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JSObject extractDataFromResultSet(Cursor cursor, Map<String, String> projectionMap) {
        try {
            JSObject result = new JSObject();
            for (Map.Entry<String, String> entry : projectionMap.entrySet()) {
                int columnIndex = cursor.getColumnIndex(entry.getKey());
                result.put(entry.getValue(), cursor.getString(columnIndex));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class VisitableCursorWrapper implements Visitable<Cursor>, Closeable, AutoCloseable {

        private Cursor cursor;

        private VisitableCursorWrapper(Cursor cursor) {
            this.cursor = cursor;
        }

        public void accept(Visitor<Cursor> visitor) {
            while (cursor != null && cursor.moveToNext()) {
                visitor.visit(cursor);
            }
        }

        @Override
        public void close() {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
