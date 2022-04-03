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
    private JSArray emailAddresses = new JSArray();
    private JSArray postalAddresses = new JSArray();

    public ContactDataExtractorVisitor(Map<String, String> projectionMap) {
        this.projectionMap = projectionMap;
    }

    @Override
    public void visit(Cursor cursor) {
        JSObject currentDataRecord = ContentQueryService.extractDataFromResultSet(cursor, projectionMap);
        String currentMimeType = currentDataRecord.getString(PluginContactFields.MIME_TYPE);

        //Log.v("HELLO", String.valueOf(currentDataRecord));

        if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(currentMimeType)) {
            JSObject email = new JSObject();
            if (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA1) != null) {
                email.put("emailAddress", currentDataRecord.getString(ContactsContract.Contacts.Data.DATA1));
            }
            switch (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA2)) {
                case "1":
                    email.put("type", "home");
                    break;
                case "2":
                    email.put("type", "mobile");
                    break;
                case "3":
                    email.put("type", "work");
                    break;
                default:
                    email.put("type", "other");
                    break;
            }
            emailAddresses.put(email);
        } else if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(currentMimeType)) {
            JSObject phone = new JSObject();
            // https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Phone
            if (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA1) != null) {
                phone.put("phoneNumber", currentDataRecord.getString(ContactsContract.Contacts.Data.DATA1));
            }
            switch (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA2)) {
                case "1":
                    phone.put("type", "home");
                    break;
                case "2":
                    phone.put("type", "mobile");
                    break;
                case "3":
                    phone.put("type", "work");
                    break;
                default:
                    phone.put("type", "other");
                    break;
            }
            phoneNumbers.put(phone);
        } else if (ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE.equals(currentMimeType)) {
            JSObject address = new JSObject();
            // https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.StructuredPostal
            if (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA1) != null) {
                address.put("formattedAddress", currentDataRecord.getString(ContactsContract.Contacts.Data.DATA1));
            }
            if (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA4) != null) {
                address.put("street", currentDataRecord.getString(ContactsContract.Contacts.Data.DATA4));
            }
            if (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA5) != null) {
                address.put("pobox", currentDataRecord.getString(ContactsContract.Contacts.Data.DATA5));
            }
            if (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA6) != null) {
                address.put("neighborhood", currentDataRecord.getString(ContactsContract.Contacts.Data.DATA6));
            }
            if (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA7) != null) {
                address.put("city", currentDataRecord.getString(ContactsContract.Contacts.Data.DATA7));
            }
            if (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA8) != null) {
                address.put("state", currentDataRecord.getString(ContactsContract.Contacts.Data.DATA8));
            }
            if (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA9) != null) {
                address.put("postalCode", currentDataRecord.getString(ContactsContract.Contacts.Data.DATA9));
            }
            if (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA10) != null) {
                address.put("country", currentDataRecord.getString(ContactsContract.Contacts.Data.DATA10));
            }
            switch (currentDataRecord.getString(ContactsContract.Contacts.Data.DATA2)) {
                case "1":
                    address.put("type", "home");
                    break;
                case "2":
                    address.put("type", "work");
                    break;
                default:
                    address.put("type", "other");
                    break;
            }
            postalAddresses.put(address);
        }
    }

    public JSArray getPhoneNumbers() {
        return phoneNumbers;
    }
    public JSArray getEmailAddresses() {
        return emailAddresses;
    }
    public JSArray getPostalAddresses() { return postalAddresses; }
}
