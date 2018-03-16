import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {ClientInfo} from "../../model/ClientInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {EditableClientInfo} from "../../model/EditableClientInfo";
import {type} from "os";

@Component({
    selector: 'main-form-component',
    template: require('./main_form.component.html'),
    styles: [require ('./main-form.component.css')],
})

export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();

  clients: ClientInfo[] = null;
  addedClientID: string = "";
  userInfo: UserInfo | null = null;
  editableClientInfo: EditableClientInfo = new EditableClientInfo();
  loadUserInfoButtonEnabled: boolean = true;
  modalViewEnabled: boolean = false;
  loadUserInfoError: string | null;
  selectedID = "";
  actionType = "";

  constructor(private httpService: HttpService) {}

  loadUserInfoButtonClicked() {
    this.loadUserInfoButtonEnabled = false;
    this.loadUserInfoError = null;

    this.httpService.get("/auth/clientsInfo").toPromise().then(result => {
      this.clients = result.json();
    }, error => {
      console.log(error);
      this.loadUserInfoButtonEnabled = true;
      this.loadUserInfoError = error;
      this.userInfo = null;
    });
  }

  selectClient(clientId : string) {
    this.selectedID = clientId;
  }

  removeClientClicked() {
    if (this.selectedID != "") {
      this.httpService.post("/auth/removeClient", {
        clientID: this.selectedID
      }).toPromise().then(res => {
        if (this.actionType == "edit") {
            this.addNewClient();
        } else {
            this.loadUserInfoButtonClicked();
        }
      }, error => {
        console.log(error);
      })
    }
  }

  editClientClicked() {
    if (this.selectedID != "") {
        this.actionType = "edit";
        this.addedClientID = this.selectedID;
        this.modalViewEnabled = true;

        let url = "/auth/editableClientInfo/" + this.selectedID;
        this.httpService.get(url).toPromise().then(result => {
            // console.log(result.json());
            this.editableClientInfo = EditableClientInfo.from(result.json() as EditableClientInfo);
            // console.log(this.editableClientInfo);
        }, error => {
            console.log(error);
        });
    }
  }

  addClientClicked() {
    this.actionType = "add";
    this.addedClientID = "c" + (this.clients.length + 1).toString();
    this.modalViewEnabled = true;
  }

  closeModal() {
    this.modalViewEnabled = false;
    this.editableClientInfo.clearPar();
  }
  editAddClicked() {
    if (this.fieldsFilledCorrectly()) {
        if (this.actionType == "edit") {
            this.removeClientClicked();
        } else {
            this.addNewClient();
        }
    } else {
      alert("Заполните все необходимые поля, помеченные звездочкой");
    }
  }
  addNewClient() {
      let clientInfo = this.createClient();

      this.httpService.post("/auth/addNewClient", {
          clientInfo  : clientInfo,
      }).toPromise().then(res => {
          this.addPhones();
      }, error => {
          console.log(error);
      });
  }
  createClient() : string {
    // console.log(this.patronymic)
    let str = this.addedClientID;
    str += "; " + this.editableClientInfo.name + " " + this.editableClientInfo.patronymic + " " + this.editableClientInfo.surname;
    str += "; " + this.editableClientInfo.gender;
    str += "; " + this.editableClientInfo.birth_date;
    str += "; " + this.editableClientInfo.charm;

    console.log(str);
    return str;
  }
  addAdresses() {
      let str = this.addedClientID;
      str += "; " + "REG; " + this.editableClientInfo.rAdressStreet + "; " + this.editableClientInfo.rAdressHouse
             + "; " + this.editableClientInfo.rAdressFlat;

      if (this.editableClientInfo.fAdressStreet != "" && this.editableClientInfo.fAdressHouse != "" &&
          this.editableClientInfo.fAdressFlat != "") {
          str += ", ";
          str += this.addedClientID + "; FACT";
          str += "; " + this.editableClientInfo.fAdressStreet;
          str += "; " + this.editableClientInfo.fAdressHouse;
          str += "; " + this.editableClientInfo.fAdressFlat;
      }

      this.httpService.post("/auth/addNewAdress", {
          adresses  : str,
      }).toPromise().then(res => {
          this.loadUserInfoButtonClicked();
          this.closeModal();
      }, error => {
          console.log(error);
      });
  }
  addPhones() {
      let str = this.addedClientID;
      str += "; " + this.editableClientInfo.mobilePhones[0] + "; MOBILE";

      if (this.editableClientInfo.workPhone != "") {
        str += ", ";
        str += this.addedClientID;
        str += "; " + this.editableClientInfo.workPhone;
        str += "; " + "WORK"
      }

      if (this.editableClientInfo.homePhone != "") {
          str += ", ";
          str += this.addedClientID;
          str += "; " + this.editableClientInfo.homePhone;
          str += "; " + "HOME"
      }

      this.httpService.post("/auth/addNewPhone", {
          phones  : str,
      }).toPromise().then(res => {
          this.addAdresses();
      }, error => {
          console.log(error);
      });
  }

  fieldsFilledCorrectly() : boolean {
        if (this.editableClientInfo.name != "" && this.editableClientInfo.surname != "" && this.editableClientInfo.gender != ""
            && this.editableClientInfo.birth_date != "" && this.editableClientInfo.charm != ""
            && this.editableClientInfo.rAdressStreet != "" && this.editableClientInfo.rAdressFlat != ""
            && this.editableClientInfo.rAdressHouse != "" && this.editableClientInfo.mobilePhones[0] != "") {
            return true;
        } else {
            return false;
        }
    }
}