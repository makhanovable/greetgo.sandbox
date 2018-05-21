import {Component, EventEmitter, OnDestroy, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {Subscription} from "rxjs/Subscription";
import {MatDialog, MatDialogConfig} from "@angular/material";
import {ModalInfoComponent} from "./components/modal-info/modal-info";

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

  constructor(private httpService: HttpService, private dialog: MatDialog) {
  }

  handleAddAccClick = function () {
    this.openModal(-1);
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
    })
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

  checkHealthButtonClicked() {
    this.httpService.get("/accounts/ok").toPromise().then(response => {
      console.log(response)
    }, error => {
      console.log(error)
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
