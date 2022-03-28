import Foundation
import Capacitor
import ContactsUI

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(ContactPicker)
public class ContactPicker: CAPPlugin, CNContactPickerDelegate {

    var vc: CNContactPickerViewController?
    var id: String?

    @objc func open(_ call: CAPPluginCall) {
        self.id = call.callbackId
        call.keepAlive = true
        Permissions.contactPermission { granted in
            if granted {
                DispatchQueue.main.async {
                    self.vc = CNContactPickerViewController()
                    self.vc!.delegate = self
                    self.bridge?.viewController?.present(self.vc!, animated: true, completion: nil)
                }
            } else {
                call.reject("User denied access to contacts")
            }
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

    func makeContact(_ contact: CNContact) -> Dictionary<String, Any> {
        var res: [String:Any] = [:]
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
        res["phoneNumberLabels"] = contact.phoneNumbers.map {
            CNLabeledValue<NSString>.localizedString(forLabel: $0.label ?? "")
        }
        res["postalAddresses"] = contact.postalAddresses.map {
            CNPostalAddressFormatter().string(from: $0.value)
        }
        res["postalAddressLabels"] = contact.postalAddresses.map() {
            CNLabeledValue<NSString>.localizedString(forLabel: $0.label ?? "")
        }

        // temporarily disable returning contact imageData because base64 data overwhelms console log
        /*if contact.imageData != nil {
            let image = UIImage(data: contact.imageData!)?.pngData() ?? UIImage(data: contact.imageData!)?.jpegData(compressionQuality: 0);
            res["image"] = image!.base64EncodedString(options: .lineLength64Characters);
        }*/

        return res
    }

    // didSelect contacts: [CNContact]
    public func contactPicker(_ picker: CNContactPickerViewController, didSelect contact: CNContact) {
        picker.dismiss(animated: true, completion: nil)
        guard let call = self.bridge?.savedCall(withID: self.id!) else {
            print("call was not loaded correctly")
            return
        }
        //print("result: " + String(describing: makeContact(contact)))
        call.resolve(makeContact(contact));
    }

    public func contactPickerDidCancel(_ picker: CNContactPickerViewController) {
        //print("closed!")
        picker.dismiss(animated: true, completion: nil)
        guard let call = self.bridge?.savedCall(withID: self.id!) else { return }
        call.resolve()
    }
}

class Permissions {
    class func contactPermission(completionHandler: @escaping (_ accessGranted: Bool) -> Void) {
        let contactStore = CNContactStore()
        switch CNContactStore.authorizationStatus(for: .contacts) {
        case .authorized:
            completionHandler(true)
        case .denied:
            completionHandler(false)
        case .restricted, .notDetermined:
            contactStore.requestAccess(for: .contacts) { granted, _ in
                if granted {
                    completionHandler(true)
                } else {
                    DispatchQueue.main.async {
                        completionHandler(false)
                    }
                }
            }
        }
    }
}
