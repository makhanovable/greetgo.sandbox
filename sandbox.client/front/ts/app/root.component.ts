import {Component, OnInit} from "@angular/core";
import {HttpService} from "./HttpService";
import {AuthInfo} from "../model/AuthInfo";

@Component({
  selector: 'root-component',
  template: require('./root-component.html'),
})

export class RootComponent implements OnInit {
  mode: string = "login";
  constructor(private httpService: HttpService) {}

  ngOnInit(): void {
    this.mode = 'init';
    this.startApp();
  }

  startApp() {
    if (!this.httpService.token) {
      this.mode = 'login';
      return;
    }

    this.httpService.get("/auth/info").toPromise().then(result => {
      let userInfo = result.json() as AuthInfo;
      if (userInfo.pageSize) this.httpService.pageSize = userInfo.pageSize;
      (<any>window).document.title = userInfo.appTitle;
      this.mode = 'main-form';
    }, error => {
      console.log(error);
      this.mode = "login";
    });

  }

  exit() {
    this.httpService.token = null;
    this.mode = 'login';
  }
}
