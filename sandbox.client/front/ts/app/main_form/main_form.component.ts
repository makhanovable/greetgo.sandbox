import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {ClientAccountInfo} from "../../model/ClientAccountInfo";

@Component({
  selector: 'main-form-component',
  template: require('./main-form.html'),
  styles: [require('./main-form.css')]
})
export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();

  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;

  accountInfoList: ClientAccountInfo[] | null = null;

  constructor(private httpService: HttpService) {
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

    this.accountInfoList = [
      ClientAccountInfo.copy({
        "fullName": "Some Full Name",
        "charm": "ch1",
        "age": 1,
        "totalAccountBalance":  5,
        "maxAccountBalance": 2,
        "minAccountBalance": 1
      }),
      ClientAccountInfo.copy({
        "fullName": "Qwerty Full Name",
        "charm": "ch1",
        "age": 1,
        "totalAccountBalance":  5,
        "maxAccountBalance": 2,
        "minAccountBalance": 1
      }),
      ClientAccountInfo.copy({
        "fullName": "World Full Name",
        "charm": "ch1",
        "age": 1,
        "totalAccountBalance":  5,
        "maxAccountBalance": 2,
        "minAccountBalance": 1
      })
    ];

    console.log(this.accountInfoList)
  }
}
