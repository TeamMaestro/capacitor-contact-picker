declare module "@capacitor/core" {
    interface PluginRegistry {
        ContactPicker: ContactPickerPlugin;
    }
}

export interface ContactPickerPlugin {

    open?(): Promise<Contact[]>;

    close?(): Promise<void>;
}

interface Contact {
    identifier?: string;
    givenName?: string;
    familyName?: string;
    nickname?: string;
    jobTitle?: string;
    departmentName?: string;
    organizationName?: string;
    note?: string;
    phoneNumbers: string[];
    emailAddresses: string[]
}
