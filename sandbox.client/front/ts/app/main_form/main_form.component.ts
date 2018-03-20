import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {ClientInfo} from "../../model/ClientInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {EditableClientInfo} from "../../model/EditableClientInfo";
import {type} from "os";
import {ClientRecord} from "../../model/ClientRecord";

@Component({
    selector: 'main-form-component',
    template: require('./main_form.component.html'),
    styles: [require ('./main-form.component.css')],
})

export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();

  clientRecords: ClientRecord[] = null;
  addedClientID: string = "";
  userInfo: UserInfo | null = null;
  editableClientInfo: EditableClientInfo = new EditableClientInfo();
  loadUserInfoButtonEnabled: boolean = true;
  modalViewEnabled: boolean = false;
  loadUserInfoError: string | null;
  filterText = "";
  selectedID = "";
  actionType = "";
  currentIndex = "1";
  pageNumber = 0;
  pagesIndex = [];

  constructor(private httpService: HttpService) {}

  loadUserInfoButtonClicked() {
    this.loadUserInfoButtonEnabled = false;
    this.loadUserInfoError = null;

    let url = "/client/clientsInfo/" + this.currentIndex + "/" + this.filterText;
      this.httpService.get(url).toPromise().then(result => {
          this.pageNumber = result.json().pageCount;
          this.addPages();
          this.clientRecords = result.json().clientInfos;

          console.log(this.clientRecords);
      }, error => {
          console.log(error);
      });
  }
  addPages() {
      this.pagesIndex = [];
      for (var i = 1; i <= this.pageNumber; i++) {
          this.pagesIndex.push(i);
      }
  }
  loadClients() {
      let url = "/client/clientsInfoPerPage/" + this.currentIndex;
      console.log(url);
      this.httpService.get(url).toPromise().then(result => {
          this.clientRecords = result.json();
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

  // TODO: изменить логику редактирования. Создать новую независимую функцию для редактирования
  removeClientClicked() {
    if (this.selectedID != "") {
      this.httpService.post("/client/removeClient", {
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

        let url = "/client/editableClientInfo/" + this.selectedID;
        this.httpService.get(url).toPromise().then(result => {
            // console.log(result.json());
            this.editableClientInfo = EditableClientInfo.from(result.json() as EditableClientInfo);
            // console.log(this.editableClientInfo);
        }, error => {
            console.log(error);
        });
    }
  }

  setPage(page: string) {
      this.currentIndex = page;
      this.loadUserInfoButtonClicked();
  }

  nextPage() {
      var x = +this.currentIndex;
      if (x < this.pageNumber) {
          x++;
          this.currentIndex = x.toString();
          this.loadUserInfoButtonClicked();
      }
  }

  prevPage() {
      var x = +this.currentIndex;
      console.log(x);
      if (x > 1) {
          x--;
          this.currentIndex = x.toString();
          this.loadUserInfoButtonClicked();
      }
    }

  addClientClicked() {
    this.actionType = "add";
    this.addedClientID = "";
    this.modalViewEnabled = true;
  }

  closeModal() {
    this.modalViewEnabled = false;
    this.editableClientInfo.clearPar();
  }
  editAddClicked() {
    console.log(this.addedClientID);
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

      // TODO: название переменной. Не забывай называть переменные как я объяснял. *ToSave, *Details, *Record. 
      // Если забыл, спроси.
      this.httpService.post("/client/addNewClient", {
          clientInfo  : clientInfo,
          clientID : this.addedClientID
      }).toPromise().then(res => {
          this.addPhones();
      }, error => {
          console.log(error);
      });
  }
                                    
  // TODO: эту функцию надо переделать. Здесь создавать только сам объект. 
  // Эта строка, если ты хочешь так сохранять в stand ДБ, должна генерироваться в stand ДБ.
  // Иначе смысл в разделении stand и real теряется.
  // Всё тоже самое и для других объектов, как address и т.д.
  createClient() : string {
    // console.log(this.patronymic)
    let str = "";
    str += this.editableClientInfo.name + " " + this.editableClientInfo.surname + " " + this.editableClientInfo.patronymic;
    str += "; " + this.editableClientInfo.gender;
    str += "; " + this.editableClientInfo.birth_date;
    str += "; " + this.editableClientInfo.charm;

    console.log(str);
    return str;
  }
  addAdresses() {
      let str = "";
      str += "REG; " + this.editableClientInfo.rAdressStreet + "; " + this.editableClientInfo.rAdressHouse
             + "; " + this.editableClientInfo.rAdressFlat;

      if (this.editableClientInfo.fAdressStreet != "" && this.editableClientInfo.fAdressHouse != "" &&
          this.editableClientInfo.fAdressFlat != "") {
          str += ", ";
          str += "FACT";
          str += "; " + this.editableClientInfo.fAdressStreet;
          str += "; " + this.editableClientInfo.fAdressHouse;
          str += "; " + this.editableClientInfo.fAdressFlat;
      }

      this.httpService.post("/client/addNewAdress", {
          adresses  : str,
          clientID : this.addedClientID
      }).toPromise().then(res => {
          this.loadUserInfoButtonClicked();
          this.closeModal();
      }, error => {
          console.log(error);
      });
  }
  addPhones() {
      let str = "";
      str += this.editableClientInfo.mobilePhones[0] + "; MOBILE";

      if (this.editableClientInfo.workPhone != "") {
        str += ", ";
        str += this.editableClientInfo.workPhone;
        str += "; " + "WORK"
      }

      if (this.editableClientInfo.homePhone != "") {
          str += ", ";
          str += this.editableClientInfo.homePhone;
          str += "; " + "HOME"
      }

      this.httpService.post("/client/addNewPhone", {
          phones  : str,
          clientID : this.addedClientID
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
