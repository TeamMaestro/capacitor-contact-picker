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
        call.save()
        DispatchQueue.main.async {
            self.vc = CNContactPickerViewController()
            self.vc!.delegate = self
//            self.bridge.presentVC(self.vc!, animated: true, completion: {
//                call.success();
//            })
            
//            self.bridge.viewController.present(self.vc!, animated: true, completion: {
//                call.success();
//            })
            
            self.bridge.viewController.present(self.vc!, animated: true, completion: nil)
            
        }
    }

    @objc func close(_ call: CAPPluginCall) {
        if vc == nil {
            call.success()
        }
        DispatchQueue.main.async {
            self.bridge.dismissVC(animated: true) {
                call.success()
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
        res["phoneNumbers"] = contact.phoneNumbers.map { $0.value.stringValue }
        res["emailAddresses"] = contact.emailAddresses.map { $0.value }
        // ****
        res["phoneNumberLabels"] = contact.phoneNumbers.map() {
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
        let call = self.bridge.getSavedCall(self.id!)
        if (call != nil) {
//            var object = JSObject()
//            object["value"] =  contacts.map { makeContact($0) }
//            call?.resolve(object);
            
            call!.resolve([
                "value": makeContact(contact)
            ])
        }
    }

    public func contactPickerDidCancel(_ picker: CNContactPickerViewController) {
        picker.dismiss(animated: true, completion: nil)
    }
}
