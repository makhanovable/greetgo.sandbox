import {Component, EventEmitter, OnDestroy, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {Subscription} from "rxjs/Subscription";
import {MatDialog, MatDialogConfig} from "@angular/material";
import {ModalInfoComponent} from "./components/modal-info/modal-info";
import {ClientInfoModel} from "../../model/ClientInfoModel";
import {Client} from "../../model/Client";
import {Address} from "../../model/Address";
import {Phone} from "../../model/Phone";
import {AddressType} from "../../model/AddressType";
import {Gender} from "../../model/Gender";
import {AccountService} from "../services/AccountService";

@Component({
  selector: 'main-form-component',
  template: require('./main-form.html'),
  styles: [require('./main-form.css')]
})
export class MainFormComponent implements OnDestroy {

  @Output() exit = new EventEmitter<void>();
  subscription: Subscription;

  userInfo: UserInfo | null = null;

  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;

  isEditMode = false;
  DUMB_ID = -1;

  constructor(private httpService: HttpService, private dialog: MatDialog, private accountService: AccountService) {}

  handleAddAccClick = function () {
    this.openModal(this.DUMB_ID);
  };

  handleEditAccClick = function (accountInfo) {
    this.openModal(accountInfo.id);
  };

  openModal(clientId) {
    this.httpService.get("/client/info", {clientId: clientId}).toPromise().then(response => {
      this.configureModal(response.json());
    }, error => {
      console.log(error);
    });
  }

  configureModal(clientInfoModel) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {clientInfoModel: clientInfoModel};

    const dialogRef = this.dialog.open(ModalInfoComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(data => {
      console.log(data);
      if(data !== 'undefined') {
        this.createNewClient(data)
      }
    })
  }

  createNewClient(data) {

    this.httpService.post("/client/create",{
      name: data.name,
      surname: data.surname,
      patronymic: data.patronymic,
      gender: data.gender,
      birthDate: data.birthDate.getTime(),
      charmId: data.charm,
      streetFact: data.streetFact,
      houseFact: data.houseFact,
      flatFact: data.flatFact,
      streetReg: data.streetReg,
      houseReg: data.houseReg,
      flatReg: data.flatReg,
      phoneHome: data.phoneHome,
      phoneWork: data.phoneWork,
      phoneMobile1: data.phoneMobile1,
      phoneMobile2: data.phoneMobile2,
      phoneMobile3: data.phoneMobile3,
    }).toPromise().then(response => {

      this.accountService.addNewAccount(response.json());

    }, error => {
      console.log(error);
    });
  }

  loadUserInfoButtonClicked() {
    this.loadUserInfoButtonEnabled = false;
    this.loadUserInfoError = null;

    this.httpService.get("/auth/userInfo").toPromise().then(result => {
      this.userInfo = UserInfo.copy(result.json());
      let phoneType: PhoneType | null = this.userInfo.phoneType;
      console.log(phoneType);
    }, error => {
      console.log(error);
      this.loadUserInfoButtonEnabled = true;
      this.loadUserInfoError = error;
      this.userInfo = null;
    });
  }


  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
