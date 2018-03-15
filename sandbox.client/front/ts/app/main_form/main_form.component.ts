import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {ClientInfo} from "../../model/ClientInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";

@Component({
    selector: 'main-form-component',
    template: require('./main_form.component.html'),
    styles: [require ('./main-form.component.css')],
})

export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();

  users: UserInfo[] = null;
  clients: ClientInfo[] = null;
  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  modalViewEnabled: boolean = false;
  loadUserInfoError: string | null;

  constructor(private httpService: HttpService) {}

  loadUserInfoButtonClicked() {
    this.loadUserInfoButtonEnabled = false;
    this.loadUserInfoError = null;

    this.httpService.get("/auth/clientsInfo").toPromise().then(result => {
      this.clients = result.json();
    }, error => {
      console.log(error);
      this.loadUserInfoButtonEnabled = true;
      this.loadUserInfoError = error;
      this.userInfo = null;
    });
  }

  addClientClicked() {
    this.modalViewEnabled = true;


  }
}
