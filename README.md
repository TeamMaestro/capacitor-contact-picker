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

async openPicker() {
    const contact: any = await ContactPicker.open();
    console.log("contacts", JSON.stringify(contact));
}

```
