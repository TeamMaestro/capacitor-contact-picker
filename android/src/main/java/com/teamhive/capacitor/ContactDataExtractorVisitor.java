package com.teamhive.capacitor;

import android.database.Cursor;
import android.provider.ContactsContract;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.teamhive.capacitor.contentQuery.ContentQueryService;
import com.teamhive.capacitor.utils.Visitor;

import java.util.Map;

public class ContactDataExtractorVisitor implements Visitor<Cursor> {

    private Map<String, String> projectionMap;

    private JSArray phoneNumbers = new JSArray();
    private JSArray emailAddresses = new JSArray();

    public ContactDataExtractorVisitor(Map<String, String> projectionMap) {
        this.projectionMap = projectionMap;
    }

    @Override
    public void visit(Cursor cursor) {
        JSObject currentDataRecord = ContentQueryService.extractDataFromResultSet(cursor, projectionMap);
        String currentMimeType = currentDataRecord.getString(PluginContactFields.MIME_TYPE);

        if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(currentMimeType)) {
            emailAddresses.put(currentDataRecord.getString(ContactsContract.Contacts.Data.DATA1));
        } else if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(currentMimeType)) {
            phoneNumbers.put(currentDataRecord.getString(ContactsContract.Contacts.Data.DATA1));
        }
    }

    public JSArray getPhoneNumbers() {
        return phoneNumbers;
    }

    public JSArray getEmailAddresses() {
        return emailAddresses;
    }
}
