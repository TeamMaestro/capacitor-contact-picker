package com.teamhive.capacitor;

import android.database.Cursor;
import com.getcapacitor.JSObject;
import com.teamhive.capacitor.contentQuery.ContentQueryService;
import com.teamhive.capacitor.utils.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactExtractorVisitor implements Visitor<Cursor> {

    private Map<String, String> projectionMap;

    private List<JSObject> contacts = new ArrayList<>();

    public ContactExtractorVisitor(Map<String, String> projectionMap) {
        this.projectionMap = projectionMap;
    }

    @Override
    public void visit(Cursor cursor) {
        JSObject contact = ContentQueryService.extractDataFromResultSet(cursor, projectionMap);
        contacts.add(contact);
    }

    public List<JSObject> getContacts() {
        return contacts;
    }
}
