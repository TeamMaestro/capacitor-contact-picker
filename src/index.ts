import { registerPlugin } from '@capacitor/core';

import type { ContactPickerPlugin } from './definitions';

const ContactPicker = registerPlugin<ContactPickerPlugin>('ContactPicker', {
    web: () => import('./web').then(m => new m.ContactPickerWeb()),
});

export * from './definitions';
export { ContactPicker };
