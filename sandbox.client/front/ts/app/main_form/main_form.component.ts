import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {ClientInfo} from "../../model/ClientInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {ClientDetails} from "../../model/ClientDetails";
import {type} from "os";
import {ClientRecord} from "../../model/ClientRecord";
import {Charm} from "../../model/Charm";
import {ClientToSave} from "../../model/ClientToSave";

@Component({
    selector: 'main-form-component',
    template: require('./main_form.component.html'),
    styles: [require ('./main-form.component.css')],
})

export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();

  clientRecords: ClientRecord[] = null;
  charmRecords: String[] = null;
  addedClientID: string = "";
  clientDetails: ClientDetails = new ClientDetails();
  clientToSave: ClientToSave;
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

  selectClient(clientId : string) {
    this.selectedID = clientId;
  }

  // TODO: изменить логику редактирования. Создать новую независимую функцию для редактирования
  removeClientClicked() {
    if (this.selectedID != "") {
      this.httpService.post("/client/removeClient", {
        clientID: this.selectedID
      }).toPromise().then(res => {

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

        let url = "/client/clientDetails/" + this.selectedID;
        this.httpService.get(url).toPromise().then(result => {
            this.clientDetails = ClientDetails.from(result.json() as ClientDetails);
            this.loadCharms()
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
    this.loadCharms()
  }

  closeModal() {
    this.modalViewEnabled = false;
    this.clientDetails.clearPar();
  }

  loadCharms() {
      this.httpService.get("/client/charms").toPromise().then(result => {
          console.log(result.json());
          this.charmRecords = result.json();
      }, error => {
          console.log(error);
      });
  }

  editAddClicked() {
    if (this.fieldsFilledCorrectly()) {
        if (this.actionType == "edit") {
            this.updateClientInfo();
        } else {
            this.addNewClient();
        }
    } else {
      alert("Заполните все необходимые поля, помеченные звездочкой");
    }
  }
  addNewClient() {
      this.clientToSave = ClientToSave.from(this.clientDetails as ClientToSave);

      // TODO: название переменной. Не забывай называть переменные как я объяснял. *ToSave, *Details, *Record. 
      // Если забыл, спроси.
      this.httpService.post("/client/addNewClient", {
          clientToSave  : JSON.stringify(this.clientToSave),
          clientID : this.addedClientID
      }).toPromise().then(res => {
          console.log(res.json());
          let clientRecord = new ClientRecord();
          clientRecord = res.json();
          this.clientRecords.push(clientRecord);
          this.selectedID = clientRecord.id;
          this.closeModal();
      }, error => {
          console.log(error);
      });
  }
  updateClientInfo() {
      this.clientToSave = ClientToSave.from(this.clientDetails as ClientToSave);

      this.httpService.post("/client/updateClient", {
          clientToSave  : JSON.stringify(this.clientToSave)
      }).toPromise().then(res => {
          console.log(res.json());
          let clientRecord = new ClientRecord();
          clientRecord = res.json();
          for (var i = 0; i < this.clientRecords.length; i++){
              if(this.clientRecords[i].id == clientRecord.id){
                  this.clientRecords[i] == clientRecord;
              }
          }
          this.closeModal();
      }, error => {
          console.log(error);
      });
  }
                                    
  // TODO: эту функцию надо переделать. Здесь создавать только сам объект. 
  // Эта строка, если ты хочешь так сохранять в stand ДБ, должна генерироваться в stand ДБ.
  // Иначе смысл в разделении stand и real теряется.
  // Всё тоже самое и для других объектов, как address и т.д.

  fieldsFilledCorrectly() : boolean {
        if (this.clientDetails.name != "" && this.clientDetails.surname != "" && this.clientDetails.gender != ""
            && this.clientDetails.birth_date != "" && this.clientDetails.charm != ""
            && this.clientDetails.rAdressStreet != "" && this.clientDetails.rAdressFlat != ""
            && this.clientDetails.rAdressHouse != "" && this.clientDetails.mobilePhones[0] != "") {
            return true;
        } else {
            return false;
        }
    }
}
