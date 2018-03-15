import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {ClientInfo} from "../../model/ClientInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";

@Component({
    selector: 'main-form-component',
    template: require('./main_form.component.html'),
    styles: [require ('./main-form.component.css')],
})

export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();

  clients: ClientInfo[] = null;
  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  modalViewEnabled: boolean = false;
  loadUserInfoError: string | null;

  id = "";
  name = "";
  surname = "";
  patronymic = "";
  gender = "";
  birth_date = "";
  charm = "";
  fAdressStreet = "";
  fAdressHouse = "";
  fAdressFlat = "";
  rAdressStreet = "";
  rAdressHouse = "";
  rAdressFlat = "";
  workPhone = "";
  homePhone = "";
  mobilePhone = "";

  selectedID : string;

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

  addClientClicked() {
    this.modalViewEnabled = true;
  }

  closeModal() {
    this.modalViewEnabled = false;
  }

  editAddClicked() {

    if (this.fieldsFilledCorrectly()) {
        this.closeModal();

        let clientInfo = this.createClient();

        this.httpService.post("/auth/addNewClient", {
            clientInfo  : clientInfo,
        }).toPromise().then(res => {
            this.addPhones();
        }, error => {
            console.log(error);
        });
    } else {
      alert("Заполните все необходимые поля, помеченные звездочкой");
    }
  }
  createClient() : string {
    // console.log(this.patronymic)
    this.id = "c" + (this.clients.length + 1).toString();
    let str = this.id;
    str += "; " + this.name + " " + this.patronymic + " " + this.surname;
    str += "; " + this.gender;
    str += "; " + this.birth_date;
    str += "; " + this.charm;

    console.log(str);
    return str;
  }
  addAdresses() {
      let str = this.id;
      str += "; " + "REG; " + this.rAdressStreet + "; " + this.rAdressHouse + "; " + this.rAdressFlat;

      if (this.fAdressStreet != "" && this.fAdressHouse != "" && this.fAdressFlat != "") {
          str += ", ";
          str += this.id + "; FACT";
          str += "; " + this.fAdressStreet;
          str += "; " + this.fAdressHouse;
          str += "; " + this.fAdressFlat;
      }

      this.httpService.post("/auth/addNewAdress", {
          adresses  : str,
      }).toPromise().then(res => {
          this.loadUserInfoButtonClicked();
          this.clearInputs();
      }, error => {
          console.log(error);
      });
  }
  addPhones() {
      let str = this.id;
      str += "; " + this.mobilePhone + "; MOBILE";

      if (this.workPhone != "") {
        str += ", ";
        str += this.id;
          str += "; " + this.workPhone;
          str += "; " + "WORK"
      }

      if (this.homePhone != "") {
          str += ", ";
          str += this.id;
          str += "; " + this.homePhone;
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

  clearInputs() {
      this.name = "";
      this.surname = "";
      this.patronymic = "";
      this.gender = "";
      this.birth_date = "";
      this.charm = "";
      this.fAdressStreet = "";
      this.fAdressHouse = "";
      this.fAdressFlat = "";
      this.rAdressStreet = "";
      this.rAdressHouse = "";
      this.rAdressFlat = "";
      this.workPhone = "";
      this.homePhone = "";
      this.mobilePhone = "";
  }

  fieldsFilledCorrectly() : boolean {
        if (this.name != "" && this.surname != "" && this.gender != "" && this.birth_date != ""
            && this.charm != "" && this.rAdressStreet != "" && this.rAdressFlat != "" && this.rAdressHouse != ""
            && this.mobilePhone != "") {
            return true;
        } else {
            return false;
        }
    }
}