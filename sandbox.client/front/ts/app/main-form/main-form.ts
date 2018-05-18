import {Component, EventEmitter, OnDestroy, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {AccountService} from "../services/AccountService";
import {Subscription} from "rxjs/Subscription";
import random = require("core-js/fn/number/random");
import {Charm} from "../../model/Charm";

@Component({
  selector: 'main-form-component',
  template: require('./main-form.html'),
  styles: [require('./main-form.css')]
})
export class MainFormComponent implements OnDestroy{

  @Output() exit = new EventEmitter<void>();
  subscription:Subscription;

  userInfo: UserInfo | null = null;

  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;
  selectedRow: number;
  setClickRow: Function;

  isEditMode = false;

  constructor(private httpService: HttpService, private testService: AccountService) {
    this.subscription = testService.getValue().subscribe(value => {});

    this.setClickRow = function(index) {
      this.selectedRow = index;
    };
  }

  openEditModal() {
    this.isEditMode = true;
    console.log(this.isEditMode);
  }

  closeEditModal() {
    this.isEditMode = false;
    console.log(this.isEditMode)
  }

  handleAddAccClick = function() {
    console.log("main handle add");
  };

  handleEditAccClick = function(accountInfo) {
    console.log("main handle edit");
    console.log(accountInfo.id);
  };

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
