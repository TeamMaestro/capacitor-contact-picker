<p align="center">
    <img width="150px" src="https://user-images.githubusercontent.com/13732623/63229908-7d8a8100-c1d3-11e9-955e-31aff33d07e1.png">
</p>

# @teamhive/capacitor-contact-picker

<img src="https://img.shields.io/npm/v/teamhive/capacitor-contact-picker?style=flat-square" />

This package allows you to use the native contact picker UI on Android or iOS for receiving contact information. iOS supports selecting multiple contacts. Android only support single selection. Both platforms will return the same payload structure, where the data exists.

## Installation
```
npm i git+ssh://git@github.com/calvinckho/capacitor-contact-picker
```

### iOS

For iOS you need to set a usage description in your info.plist file. (Privacy Setting)
Open xCode search for your info.plist file and press the tiny "+". Add the following entry:

```
Privacy - Contacts Usage Description
```

Give it a value like:

```
"We need access to your contacts in order to do something."
```

Also, on iOS it is a best practice to ask for permission twice. See [here](https://blog.prototypr.io/3-best-practices-for-in-app-permissions-dce7d36544a4). 

### Android
Add users permission in `AndroidManifest.xml`:
```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
      package="com.mycompany.app">
+     <uses-permission android:name="android.permission.READ_CONTACTS" />
</manifest>    
```

## Capacitor 3 Usage
```ts
import { ContactPicker } from '@teamhive/capacitor-contact-picker';

try {
    const contact: any = await ContactPicker.open();
    /* method returns a JSON contact object or undefined if no contact was selected
    sample contact object:
    {
        "displayName":"John Appleseed",
        "contactId":"410FE041-5C4E-48DA-B4DE-04C15EA3DBAC",
        "organizationName":"",
        "jobTitle":"",
        "emailAddresses":[{"type":"work","emailAddress":"John-Appleseed@mac.com"}],
        "givenName":"John",
        "note":"College roommate",
        "phoneNumbers":[{"type":"mobile","phoneNumber":"888-555-5512"},{"type":"home","phoneNumber":"888-555-1212"}],
        "familyName":"Appleseed",
        "postalAddresses":[{"postalCode":"30303","street":"3494 Kuhl Avenue","formattedAddress":"3494 Kuhl Avenue\nAtlanta GA 30303\nUSA","type":"work","state":"GA","city":"Atlanta","country":"USA","isoCountryCode":"us"},{"city":"Atlanta","street":"1234 Laurel Street","formattedAddress":"1234 Laurel Street\nAtlanta GA 30303\nUSA","type":"home","state":"GA","postalCode":"30303","country":"USA","isoCountryCode":"us"}],
        "departmentName":"",
        "nickname":""
    }*/
} catch(err) {
    // handle method rejection when permission is not granted
}

```
### Returned Object Properties:
```
export interface Contact {
    identifier?: string;
    androidContactLookupKey?: string; // Android only
    contactId?: string; // iOS only
    givenName?: string;
    familyName?: string;
    nickname?: string;
    fullName?: string;
    jobTitle?: string;
    departmentName?: string;
    organizationName?: string;
    note?: string;
    emailAddresses: [{
        type: String,
        emailAddress: String,
    }]
    phoneNumbers: [{
        type: String,
        phoneNumber: String,
    }]
    postalAddresses: [{
        type: String,
        formattedAddress: String,
        street: String,
        pobox: String, // Android only
        neighborhood: String, // Android only
        city: String,
        state: String,
        postalCode: String,
        country: String,
        isoCountryCode: String, // iOS only
        subAdministrativeArea: String, // iOS only
        subLocality: String, // iOS only
    }]
}
```
