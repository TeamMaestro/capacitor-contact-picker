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
        "fullName":"Joe Smith",
        "displayName":"Joe Smith",
        "givenName":"Joe",
        "familyName":"Smith",
        "emailAddresses":["joe@smith.com","joesmith@gmail.com","joesmith@yahoo.com"],
        "phoneNumbers":["+1 (990) 881-1283","+1 (510) 856-0722","+1 (250) 551-0748","+18009811483"],
        "phoneNumberLabels":["mobile","other","work","mobile"],
        "postalAddresses":["1 Market Street\nSan Francisco, CA 94544","PO 21064\nOakland, CA 94080"],
        "postalAddressLabels":["home","home"]
    }*/
} catch(err) {
    // handle method rejection when permission is not granted
}

```
