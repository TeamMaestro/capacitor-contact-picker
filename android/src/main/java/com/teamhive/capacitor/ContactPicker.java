package com.teamhive.capacitor;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds;

import java.util.HashMap;
import java.util.Map;

@NativePlugin(
    permissions={ Manifest.permission.READ_CONTACTS },
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

    private static final String[] CONTACT_FIELDS_PROJECTION;
    private static final Map<String, String> CONTACT_FIELDS_MAP = new HashMap<String, String>();

    static {
        CONTACT_FIELDS_MAP.put(CommonDataKinds.Phone.DISPLAY_NAME, "displayName");
        CONTACT_FIELDS_MAP.put(CommonDataKinds.Email.ADDRESS, "emailAddress");
        CONTACT_FIELDS_PROJECTION = CONTACT_FIELDS_MAP.keySet().toArray(new String[]{});
    }

    @PluginMethod()
    public void open(PluginCall call) {
        if (!hasRequiredPermissions()) {
            saveCall(call);
            NativePlugin annotation = handle.getPluginAnnotation();
            pluginRequestPermissions(annotation.permissions(), REQUEST_OPEN_CODE);
            return;
        }
        saveCall(call);
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK);
        contactPickerIntent.setType(CommonDataKinds.Email.CONTENT_TYPE);
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

        for (int result: grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                savedCall.error("User denied permission");
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
            Cursor cursor = null;
            try {
                cursor = getContext().getContentResolver().query(intent.getData(), CONTACT_FIELDS_PROJECTION, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    JSObject tempContact = new JSObject();
                    try {
                        for (Map.Entry<String, String> entry : CONTACT_FIELDS_MAP.entrySet()) {
                            int columnIndex = cursor.getColumnIndex(entry.getKey());
                            tempContact.put(entry.getValue(), cursor.getString(columnIndex));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    JSObject contact = new JSObject();

                    JSArray emailAddresses = new JSArray();
                    emailAddresses.put(tempContact.getString("emailAddress"));

                    String displayName = tempContact.getString("displayName");
                    contact.put("emailAddresses", emailAddresses);
                    contact.put("givenName", displayName.split(" ")[0]);
                    contact.put("familyName", displayName.split(" ")[1]);

                    JSObject result = new JSObject();
                    result.put("value", contact);
                    savedCall.success(result);
                }
            } catch (Exception e) {

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

}
