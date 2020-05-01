<p align="center">
    <img width="150px" src="https://user-images.githubusercontent.com/13732623/63229908-7d8a8100-c1d3-11e9-955e-31aff33d07e1.png">
</p>

# @teamhive/capacitor-contact-picker

<img src="https://img.shields.io/npm/v/teamhive/capacitor-contact-picker?style=flat-square" />

This package allows you to use the native contact picker UI on Android or iOS for receiving contact information. iOS supports selecting multiple contacts. Android only support single selection. Both platforms will return the same payload structure, where the data exists.

## Installation
```
yarn add @teamhive/capacitor-contact-picker
// or
npm i @teamhive/capacitor-contact-picker
```

### Android
Register the plugin class in your `MainActivity.java`:
```java
import com.teamhive.capacitor.ContactPicker;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        ...
        add(ContactPicker.class);
    }
}
```

## Usage
```ts
import { Plugins } from '@capacitor/core';

const { ContactPicker } = Plugins;

async openPicker() {
    const res = await ContactPicker.open();
    // res.value is an array of contacts
}

```
