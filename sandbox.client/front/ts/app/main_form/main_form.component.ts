import { Gender } from './../../model/ClientInfo';
import { Component, EventEmitter, Output } from "@angular/core";
import { UserInfo } from "../../model/UserInfo";
import { HttpService } from "../HttpService";
import { MatTableDataSource, MatPaginator } from "@angular/material";
import { ViewChild } from "@angular/core/src/metadata/di";
import { ClientInfo } from "../../model/ClientInfo";

@Component({
  selector: 'main-form-component',
  template: require('./main_form-component.html'),
  styles: [require('./main_form-component.css')],
})
export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();

  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;

  displayedColumns = ['id', 'FIO', 'gender', 'charm'];
  dataSource = new MatTableDataSource<ClientInfo>(ELEMENT_DATA);

  constructor(private httpService: HttpService) { }

  loadUserInfoButtonClicked() {
    this.loadUserInfoButtonEnabled = false;
    this.loadUserInfoError = null;

    this.httpService.get("/auth/userInfo").toPromise().then(result => {
      this.userInfo = new UserInfo().assign(result.json() as UserInfo);
    }, error => {
      console.log(error);
      this.loadUserInfoButtonEnabled = true;
      this.loadUserInfoError = error;
      this.userInfo = null;
    });
  }

  pong() {
    alert('hello')
  }
  notImpl(){
    alert("not Implemented")
  }

}

const ELEMENT_DATA: any[] = [
   { age: "21", name: "Dauren", surName: "amze", gender: Gender.MALE, charm: { name: "ленивый" } }
  , { age: "21", name: "Dauren", surName: "amze", gender: Gender.MALE, charm: { name: "ленивый" } }
  , { age: "21", name: "Dauren", surName: "amze", gender: Gender.MALE, charm: { name: "ленивый" } }
  , { age: "21", name: "Dauren", surName: "amze", gender: Gender.MALE, charm: { name: "ленивый" } }
  , { age: "21", name: "Dauren", surName: "amze", gender: Gender.MALE, charm: { name: "ленивый" } }
  , { age: "21", name: "Dauren", surName: "amze", gender: Gender.MALE, charm: { name: "ленивый" } }
  , { age: "21", name: "Dauren", surName: "amze", gender: Gender.MALE, charm: { name: "ленивый" } }

];