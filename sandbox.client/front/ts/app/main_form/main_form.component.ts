import {Component, EventEmitter, OnDestroy, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {ClientAccountInfo} from "../../model/ClientAccountInfo";
import {TestService} from "../service/testService";
import {Subscription} from "rxjs/Subscription";
import random = require("core-js/fn/number/random");

@Component({
  selector: 'main-form-component',
  template: require('./main-form.html'),
  styles: [require('./main-form.css')]
})
export class MainFormComponent implements OnDestroy{
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  @Output() exit = new EventEmitter<void>();

  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;

  accountInfoList: ClientAccountInfo[] | null = null;
  subscription:Subscription;
  constructor(private httpService: HttpService, private testService: TestService) {
    this.subscription = testService.getValue().subscribe(value => {

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

  loadAccountDataClicked() {
    // this.accountInfoList = [
    //   ClientAccountInfo.copy({
    //     "fullName": "Some Full Name",
    //     "charm": "ch1",
    //     "age": 1,
    //     "totalAccountBalance":  5,
    //     "maxAccountBalance": 2,
    //     "minAccountBalance": 1
    //   }),
    //   ClientAccountInfo.copy({
    //     "fullName": "Qwerty Full Name",
    //     "charm": "ch1",
    //     "age": 1,
    //     "totalAccountBalance":  5,
    //     "maxAccountBalance": 2,
    //     "minAccountBalance": 1
    //   }),
    //   ClientAccountInfo.copy({
    //     "fullName": "World Full Name",
    //     "charm": "ch1",
    //     "age": 1,
    //     "totalAccountBalance":  5,
    //     "maxAccountBalance": 2,
    //     "minAccountBalance": 1
    //   })
    // ];

    this.httpService.get("/accounts/").toPromise().then(response => {
        console.log(response)
        this.accountInfoList = response.json();

        console.log(this.accountInfoList)

      }, error => {
      console.log(error)
    });
  }

  loadClientInfoClicked() {
    const clientId = 77;

    this.httpService.get("/accounts/info", {clientId: clientId}).toPromise().then(response => {
      console.log(response)
    }, error => {
      console.log(error)
    });
  }

  checkHealthButtonClicked() {
    this.httpService.get("/accounts/ok").toPromise().then(response => {
      console.log(response)
    }, error => {
      console.log(error)
    });
  }
}
