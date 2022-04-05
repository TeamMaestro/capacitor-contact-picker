package com.teamhive.capacitor.contentQuery;

import android.net.Uri;
import android.os.CancellationSignal;

import java.util.Map;

public class ContentQuery {

    private Uri uri;
    private Map<String, String> projection;
    private String selection;
    private String[] selectionArgs;
    private String sortOrder;
    private CancellationSignal cancellationSignal;

    private ContentQuery() {
    }

    public Uri getUri() {
        return uri;
    }

    public Map<String, String> getProjection() {
        return projection;
    }

    public String getSelection() {
        return selection;
    }

    public String[] getSelectionArgs() {
        return selectionArgs;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public CancellationSignal getCancellationSignal() {
        return cancellationSignal;
    }

    public static class Builder {

        private ContentQuery contentQuery = new ContentQuery();

        public Builder withUri(Uri uri) {
            contentQuery.uri = uri;
            return this;
        }

        public Builder withProjection(Map<String, String> projection) {
            contentQuery.projection = projection;
            return this;
        }

        public Builder withSelection(String selection) {
            contentQuery.selection = selection;
            return this;
        }

        public Builder withSelectionArgs(String[] selectionArgs) {
            contentQuery.selectionArgs = selectionArgs;
            return this;
        }

        public Builder withSortOrder(String sortOrder) {
            contentQuery.sortOrder = sortOrder;
            return this;
        }

        public Builder withCancellationSignal(CancellationSignal cancellationSignal) {
            contentQuery.cancellationSignal = cancellationSignal;
            return this;
        }

        public ContentQuery build() {
            return contentQuery;
        }

    }
}
