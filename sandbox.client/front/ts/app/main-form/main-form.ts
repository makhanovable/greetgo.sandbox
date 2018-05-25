import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";

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

  constructor(private httpService: HttpService) {}

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

}
