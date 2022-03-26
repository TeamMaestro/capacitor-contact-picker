import Foundation
import Capacitor
import ContactsUI

typealias JSObject = [String:Any]

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(ContactPicker)
public class ContactPicker: CAPPlugin, CNContactPickerDelegate {

    var vc: CNContactPickerViewController?
    var id: String?

    @objc func open(_ call: CAPPluginCall) {
        id = call.callbackId
        call.keepAlive = true
        DispatchQueue.main.async {
            self.vc = CNContactPickerViewController()
            self.vc!.delegate = self
            self.bridge?.viewController?.present(self.vc!, animated: true, completion: nil)
        }
    }

    @objc func close(_ call: CAPPluginCall) {
        if vc == nil {
            call.resolve()
        }
        DispatchQueue.main.async {
            self.bridge?.dismissVC(animated: true) {
                call.resolve()
            }
        }
    }

    func makeContact(_ contact: CNContact) -> JSObject {
        var res = JSObject()
        res["contactId"] = contact.identifier;
        res["givenName"] = contact.givenName;
        res["familyName"] = contact.familyName;
        res["nickname"] = contact.nickname;
        res["displayName"] = contact.givenName + " " + contact.familyName;
        res["jobTitle"] = contact.jobTitle;
        res["departmentName"] = contact.departmentName;
        res["organizationName"] = contact.organizationName;
        res["note"] = contact.note;
        res["emailAddresses"] = contact.emailAddresses.map { $0.value }
        res["phoneNumbers"] = contact.phoneNumbers.map { $0.value.stringValue }
        res["phoneNumberLabels"] = contact.phoneNumbers.map() {
            return CNLabeledValue<NSString>.localizedString(forLabel: $0.label ?? "");
        }
        res["postalAddresses"] = contact.postalAddresses.map { $0.value }
        res["postalAddressLabels"] = contact.postalAddresses.map() {
            return CNLabeledValue<NSString>.localizedString(forLabel: $0.label ?? "");
        }
//        res["image"] = UIImage(data: contact.imageData!)?.pngData();

        if contact.imageData != nil {
            let image = UIImage(data: contact.imageData!)?.pngData() ?? UIImage(data: contact.imageData!)?.jpegData(compressionQuality: 0);
            res["image"] = image!.base64EncodedString(options: .lineLength64Characters);
        }

        return res
    }

    // didSelect contacts: [CNContact]
    public func contactPicker(_ picker: CNContactPickerViewController, didSelect contact: CNContact) {
        picker.dismiss(animated: true, completion: nil)
        let call = self.bridge?.savedCall(withID: self.id!)
        if (call != nil) {
//            var object = JSObject()
//            object["value"] =  contacts.map { makeContact($0) }
//            call?.resolve(object);

            call!.resolve(makeContact(contact))
        } else {
            call!.resolve()
        }
    }

    public func contactPickerDidCancel(_ picker: CNContactPickerViewController) {
        print("closed!")
        let call = self.bridge?.savedCall(withID: self.id!)

        call!.resolve()

        picker.dismiss(animated: true, completion: nil)
    }
}
