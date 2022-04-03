package com.teamhive.capacitor;

import android.Manifest;
import android.content.Intent;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.Log;

import androidx.activity.result.ActivityResult;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.getcapacitor.annotation.ActivityCallback;

import com.teamhive.capacitor.contentQuery.ContentQuery;
import com.teamhive.capacitor.contentQuery.ContentQueryService;
import com.teamhive.capacitor.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import android.util.Log;

@CapacitorPlugin(
    name= "ContactPicker",
    permissions={
        @Permission(strings = {Manifest.permission.READ_CONTACTS}, alias = "contacts"),
    }
  )
public class ContactPicker extends Plugin {

    // Messages
    public static final String ERROR_READ_CONTACT = "Unable to read contact data.";
    public static final String ERROR_NO_PERMISSION = "User denied permission";

    // Queries
    public static final String CONTACT_DATA_SELECT_CLAUSE = ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " IN('" + CommonDataKinds.Email.CONTENT_ITEM_TYPE + "', '" + CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "', '" + CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE + "')"; //

    @PluginMethod()
    public void open(PluginCall call) {
        if (getPermissionState("contacts") != PermissionState.GRANTED) {
            //saveCall(call);
            call.setKeepAlive(true);
            requestPermissionForAlias("contacts", call, "contactsPermsCallback");
            return;
        }
        //saveCall(call);
        call.setKeepAlive(true);
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(call, contactPickerIntent, "activityCallback");
    }

    @PermissionCallback
    private void contactsPermsCallback(PluginCall call) {
        if (getPermissionState("contacts") == PermissionState.GRANTED) {
            open(call);
        } else {
            call.reject(ERROR_NO_PERMISSION);
        }
    }

    @ActivityCallback
    private void activityCallback(PluginCall call, ActivityResult result) {
        try {
            //Log.v("File: ", String.valueOf(result));
            JSObject contact = readContactData(result.getData(), call);
            call.resolve(contact); // since it is just the contact object and not an array, no need to return the value field but instead just return the selected contact. use contact to replace Utils.wrapIntoResult(contact)
        } catch (IOException e) {
            call.reject(ERROR_READ_CONTACT);
        }
    }

    @PluginMethod()
    public void close(PluginCall call) {
        call.unimplemented();
    }

    private JSObject readContactData(Intent intent, PluginCall savedCall) throws IOException {
        final Map<String, String> projectionMap = getContactProjectionMap(); ////

        try {
            ContentQuery contactQuery = new ContentQuery.Builder()
                .withUri(intent.getData())
                .withProjection(projectionMap)
                .build();


            try (ContentQueryService.VisitableCursorWrapper contactVcw = ContentQueryService.query(getContext(), contactQuery)) {

                ContactExtractorVisitor contactExtractor = new ContactExtractorVisitor(projectionMap);
                contactVcw.accept(contactExtractor);
                List<JSObject> contacts = contactExtractor.getContacts();

                if (contacts.size() == 0) {
                    return null;
                } else {
                    JSObject chosenContact = contacts.get(0);

                    Map<String, String> dataProjectionMap = getContactDataProjectionMap(); ////////
                    ContentQuery contactDataQuery = new ContentQuery.Builder()
                        .withUri(ContactsContract.Data.CONTENT_URI)
                        .withProjection(dataProjectionMap)
                        .withSelection(CONTACT_DATA_SELECT_CLAUSE)
                        .withSelectionArgs(new String[]{chosenContact.getString(PluginContactFields.ANDROID_CONTACT_LOOKUP_KEY)})
                        .withSortOrder(ContactsContract.Data.MIMETYPE)
                        .build();

                    try (ContentQueryService.VisitableCursorWrapper dataVcw = ContentQueryService.query(getContext(), contactDataQuery)) {

                        ContactDataExtractorVisitor contactDataExtractor = new ContactDataExtractorVisitor(dataProjectionMap);
                        dataVcw.accept(contactDataExtractor);

                        return transformContactObject(chosenContact, contactDataExtractor.getEmailAddresses(), contactDataExtractor.getPhoneNumbers(), contactDataExtractor.getPostalAddresses());
                    }
                }
            }
        }  catch (Exception e) {
            return null;
        }

    }

    private JSObject transformContactObject(JSObject tempContact, JSArray emailAddresses, JSArray phoneNumbers, JSArray postalAddresses) {
        JSObject contact = new JSObject();
        contact.put(PluginContactFields.IDENTIFIER, tempContact.getString(PluginContactFields.IDENTIFIER));
        contact.put(PluginContactFields.ANDROID_CONTACT_LOOKUP_KEY, tempContact.getString(PluginContactFields.ANDROID_CONTACT_LOOKUP_KEY));
        String displayName = tempContact.getString(PluginContactFields.DISPLAY_NAME);
        contact.put(PluginContactFields.FULL_NAME, displayName);
        if (displayName != null && displayName.contains(" ")) {
            contact.put(PluginContactFields.DISPLAY_NAME, displayName);
            contact.put(PluginContactFields.GIVEN_NAME, displayName.split(" ")[0]);
            contact.put(PluginContactFields.FAMILY_NAME, displayName.split(" ")[1]);
        }
        contact.put(PluginContactFields.EMAIL_ADDRESSES, emailAddresses);
        contact.put(PluginContactFields.PHONE_NUMBERS, phoneNumbers);
        contact.put(PluginContactFields.POSTAL_ADDRESSES, postalAddresses);
        //contact.put(PluginContactFields.PHOTO_URI, tempContact.getString(PluginContactFields.PHOTO_URI));
        return contact;
    }

    private Map<String, String> getContactProjectionMap() {
        Map<String, String> contactFieldsMap = new HashMap<>();
        contactFieldsMap.put(ContactsContract.Contacts._ID, PluginContactFields.IDENTIFIER);
        contactFieldsMap.put(ContactsContract.Contacts.LOOKUP_KEY, PluginContactFields.ANDROID_CONTACT_LOOKUP_KEY);
        contactFieldsMap.put(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, PluginContactFields.DISPLAY_NAME);

         contactFieldsMap.put(ContactsContract.Contacts.PHOTO_URI, PluginContactFields.PHOTO_URI);

//        contactFieldsMap.put(ContactsContract.Contacts.Data.DATA15, PluginContactFields.PHOTO_URI);
        return contactFieldsMap;
    }

    private Map<String, String> getContactDataProjectionMap() {
        Map<String, String> contactFieldsMap = new HashMap<>();
        contactFieldsMap.put(CommonDataKinds.Email.MIMETYPE, PluginContactFields.MIME_TYPE);
        contactFieldsMap.put(ContactsContract.Data.DATA1, ContactsContract.Data.DATA1);
        contactFieldsMap.put(ContactsContract.Data.DATA2, ContactsContract.Data.DATA2);
        contactFieldsMap.put(ContactsContract.Data.DATA3, ContactsContract.Data.DATA3);
        contactFieldsMap.put(ContactsContract.Data.DATA4, ContactsContract.Data.DATA4);
        contactFieldsMap.put(ContactsContract.Data.DATA5, ContactsContract.Data.DATA5);
        contactFieldsMap.put(ContactsContract.Data.DATA6, ContactsContract.Data.DATA6);
        contactFieldsMap.put(ContactsContract.Data.DATA7, ContactsContract.Data.DATA7);
        contactFieldsMap.put(ContactsContract.Data.DATA8, ContactsContract.Data.DATA8);
        contactFieldsMap.put(ContactsContract.Data.DATA9, ContactsContract.Data.DATA9);
        contactFieldsMap.put(ContactsContract.Data.DATA10, ContactsContract.Data.DATA10);
        return contactFieldsMap;
    }

}
