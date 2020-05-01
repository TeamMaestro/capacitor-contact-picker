import { WebPlugin } from '@capacitor/core';
import { ContactPickerPlugin } from './definitions';

export class ContactPickerWeb extends WebPlugin implements ContactPickerPlugin {
    constructor() {
        super({
            name: 'ContactPicker',
            platforms: ['web']
        });
    }

    async close() {

    }
}

const ContactPicker = new ContactPickerWeb();

export { ContactPicker };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(ContactPicker);
