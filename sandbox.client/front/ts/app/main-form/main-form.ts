import {Component, EventEmitter, OnDestroy, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {Subscription} from "rxjs/Subscription";
import {MatDialog, MatDialogConfig} from "@angular/material";
import {ModalInfoComponent} from "./components/modal-info/modal-info";
import {ActionType} from "../../model/ActionType";
import {Constants} from "../../Constants";

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

  constructor(private httpService: HttpService, private dialog: MatDialog) {
  }

  handleCreateAccClick = function () {
    this.openModal(Constants.DUMB_ID, ActionType.CREATE);
  };

  handleEditAccClick = function (accountInfo) {
    this.openModal(accountInfo.id, ActionType.EDIT);
  };

  openModal(clientId: number, actionType: ActionType) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {clientId: clientId, actionType: actionType};

    const dialogRef = this.dialog.open(ModalInfoComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(data => {
      console.log(data);
    })
  }

  loadUserInfoButtonClicked() {
    this.loadUserInfoButtonEnabled = false;
    this.loadUserInfoError = null;

    this.requestUserInfo();
  }

  private requestUserInfo() {
    this.httpService.get("/auth/userInfo").toPromise().then(response => {
      this.onUserInfoRequestSuccess(response);
    }, error => {
      this.onUserInfoRequestError(error);
    });
  }

  private onUserInfoRequestSuccess(response) {
    this.userInfo = UserInfo.copy(response.json());
    let phoneType: PhoneType | null = this.userInfo.phoneType;
    console.log(phoneType);
  }

  private onUserInfoRequestError(error) {
    console.log(error);
    this.loadUserInfoButtonEnabled = true;
    this.loadUserInfoError = error;
    this.userInfo = null;
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
