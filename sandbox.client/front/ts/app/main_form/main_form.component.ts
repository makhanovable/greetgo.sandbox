import {Component, EventEmitter, Output} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientDetails} from "../../model/ClientDetails";
import {ClientRecord} from "../../model/ClientRecord";
import {ClientToSave} from "../../model/ClientToSave";

@Component({
    selector: 'main-form-component',
    template: require('./main_form.component.html'),
    styles: [require ('./main-form.component.css')],
})

export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();

  clientRecords: ClientRecord[] = null;
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

  //TODO: название функции несет неясную формулировку. По коду ты получаешь детальную информацию клента и видимо здесь появляется модалка.
  //Но, по названию кажется, что уже должно происходить само редактирование пользователя.
  openEditClientFormClicked() {
    if (this.selectedID != "") {
        this.actionType = "edit";
        this.modalViewEnabled = true;
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

  //TODO: название функции несет неясную формулировку. Видимо здесь появляется форма добавления нового пользователя.
  //Но, по названию кажется, что уже должно происходить само добавления.
  openAddClientFormClicked() {
    this.actionType = "add";
    this.modalViewEnabled = true;
  }

  closeModal() {
    this.modalViewEnabled = false;
  }

  editAddClicked(clientDetails: ClientDetails) {
      if (this.actionType == "add") {
          this.addNewClient(clientDetails);
      } else {
          this.updateClientInfo(clientDetails);
      }
  }
  addNewClient(clientDetails: ClientDetails) {
      this.clientToSave = ClientToSave.from(clientDetails as ClientToSave);

      //TODO: ID клиента должно генерироваться на сервере.
      this.httpService.post("/client/addNewClient", {
          clientToSave  : JSON.stringify(this.clientToSave)
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
  updateClientInfo(clientDetails: ClientDetails) {
      this.clientToSave = ClientToSave.from(clientDetails as ClientToSave);

      this.httpService.post("/client/updateClient", {
          clientToSave  : JSON.stringify(this.clientToSave)
      }).toPromise().then(res => {
          console.log(res.json());
          let clientRecord = new ClientRecord();
          clientRecord = res.json();
          for (var i = 0; i < this.clientRecords.length; i++){
              if(this.clientRecords[i].id == clientRecord.id){
                  //TODO: опечатка?
                  this.clientRecords[i] = clientRecord;
              }
          }
          this.closeModal();
      }, error => {
          console.log(error);
      });
  }
}
