package com.teamhive.capacitor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import com.getcapacitor.*;
import com.teamhive.capacitor.contentQuery.ContentQuery;
import com.teamhive.capacitor.contentQuery.ContentQueryService;
import com.teamhive.capacitor.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import android.util.Log;

@NativePlugin(
    permissions = {Manifest.permission.READ_CONTACTS},
    requestCodes = {
        ContactPicker.REQUEST_OPEN_CODE,
        ContactPicker.REQUEST_FETCH_CODE,
        ContactPicker.REQUEST_PERMISSIONS_CODE
    }
)
public class ContactPicker extends Plugin {

    // Request codes
    protected static final int REQUEST_OPEN_CODE = 11222;
    protected static final int REQUEST_FETCH_CODE = 10012;
    protected static final int REQUEST_PERMISSIONS_CODE = 10312;

    // Messages
    public static final String ERROR_READ_CONTACT = "Unable to read contact data.";
    public static final String ERROR_NO_PERMISSION = "User denied permission";

    // Queries
    public static final String CONTACT_DATA_SELECT_CLAUSE = ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " IN('" + CommonDataKinds.Email.CONTENT_ITEM_TYPE + "', '" + CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "')"; //

    @PluginMethod()
    public void open(PluginCall call) {
        if (!hasRequiredPermissions()) {
            saveCall(call);
            NativePlugin annotation = handle.getPluginAnnotation();
            pluginRequestPermissions(annotation.permissions(), REQUEST_OPEN_CODE);
            return;
        }
        saveCall(call);
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(call, contactPickerIntent, REQUEST_OPEN_CODE);
    }

    @PluginMethod()
    public void close(PluginCall call) {
        call.unimplemented();
    }

    @Override
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.handleRequestPermissionsResult(requestCode, permissions, grantResults);

        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            return;
        }

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                savedCall.error(ERROR_NO_PERMISSION);
                return;
            }
        }

        switch (requestCode) {
            case REQUEST_OPEN_CODE:
                open(savedCall);
                return;
        }
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent intent) {
        super.handleOnActivityResult(requestCode, resultCode, intent);
        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            return;
        }
        if (requestCode == REQUEST_OPEN_CODE) {
            try {
                JSObject contact = readContactData(intent, savedCall);
                savedCall.success(Utils.wrapIntoResult(contact));
            } catch (IOException e) {
                savedCall.error(ERROR_READ_CONTACT, e);
            }
        }
    }

    private JSObject readContactData(Intent intent, PluginCall savedCall) throws IOException {
        final Map<String, String> projectionMap = getContactProjectionMap(); ////
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

                    return transformContactObject(chosenContact, contactDataExtractor.getEmailAddresses(), contactDataExtractor.getPhoneNumbers(), contactDataExtractor.getPhoneTypes());
                }
            }
        }
    }

    private JSObject transformContactObject(JSObject tempContact, JSArray emailAddresses, JSArray phoneNumbers, JSArray phoneTypes) {
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
        contact.put(PluginContactFields.PHONE_TYPES, phoneTypes);

        // contact.put(PluginContactFields.PHOTO_URI, tempContact.getString(PluginContactFields.PHOTO_URI));
        contact.put(PluginContactFields.PHOTO_URI, tempContact.getString(PluginContactFields.PHOTO_URI));
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
        return contactFieldsMap;
    }

}
