package com.teamhive.capacitor;

import android.database.Cursor;
import android.provider.ContactsContract;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.teamhive.capacitor.contentQuery.ContentQueryService;
import com.teamhive.capacitor.utils.Visitor;

import java.util.Map;
import android.util.Log;

public class ContactDataExtractorVisitor implements Visitor<Cursor> {

    private Map<String, String> projectionMap;

    private JSArray phoneNumbers = new JSArray();
    private JSArray phoneTypes = new JSArray();
    private JSArray emailAddresses = new JSArray();
    private JSArray postalAddresses = new JSArray();
    private JSArray postalTypes = new JSArray();

    public ContactDataExtractorVisitor(Map<String, String> projectionMap) {
        this.projectionMap = projectionMap;
    }

    @Override
    public void visit(Cursor cursor) {
        JSObject currentDataRecord = ContentQueryService.extractDataFromResultSet(cursor, projectionMap);
        String currentMimeType = currentDataRecord.getString(PluginContactFields.MIME_TYPE);

        Log.v("HELLO", String.valueOf(currentDataRecord));

        if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(currentMimeType)) {
            emailAddresses.put(currentDataRecord.getString(ContactsContract.Contacts.Data.DATA1));
        } else if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(currentMimeType)) {
            phoneNumbers.put(currentDataRecord.getString(ContactsContract.Contacts.Data.DATA1));

            // https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Phone
            switch (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA2)) {
                case "1":
                    phoneTypes.put("home");
                    break;
                case "2":
                    phoneTypes.put("mobile");
                    break;
                case "3":
                    phoneTypes.put("work");
                    break;
                default:
                    phoneTypes.put("other");
                    break;
            }
        } else if (ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE.equals(currentMimeType)) {
            postalAddresses.put(currentDataRecord.getString(ContactsContract.Contacts.Data.DATA1));

            // https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.StructuredPostal
            switch (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA2)) {
                case "1":
                    postalTypes.put("home");
                    break;
                case "2":
                    postalTypes.put("work");
                    break;
                default:
                    postalTypes.put("other");
                    break;
            }
        }
    }

    public JSArray getPhoneNumbers() {
        return phoneNumbers;
    }

    public JSArray getPhoneTypes() {
        return phoneTypes;
    }

    public JSArray getEmailAddresses() {
        return emailAddresses;
    }

    public JSArray getPostalAddresses() {
        return postalAddresses;
    }

    public JSArray getPostalTypes() {
        return postalTypes;
    }
}
