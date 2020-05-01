import { WebPlugin } from '@capacitor/core';
import { ContactPickerPlugin } from './definitions';
export declare class ContactPickerWeb extends WebPlugin implements ContactPickerPlugin {
    constructor();
    close(): Promise<void>;
}
declare const ContactPicker: ContactPickerWeb;
export { ContactPicker };
