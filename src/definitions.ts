export interface ContactPickerPlugin {
    open?(): Promise<Contact[]>;
    close?(): Promise<void>;
}

export interface Contact {
    identifier?: string;
    androidContactLookupKey?: string;
    contactId?: string;
    givenName?: string;
    familyName?: string;
    nickname?: string;
    fullName?: string;
    jobTitle?: string;
    departmentName?: string;
    organizationName?: string;
    note?: string;
    phoneNumbers?: any[];
    emailAddresses?: any[]
    postalAddresses?: any[]
}
