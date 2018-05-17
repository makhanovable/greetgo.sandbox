import {Component, OnInit} from "@angular/core";
import {HttpService} from "./HttpService";
import {AuthInfo} from "../model/AuthInfo";

// import "@angular/material/prebuilt-themes/indigo-pink.css";
// import "../../../node_modules/hammerjs/hammer.js"

@Component({
  selector: 'root-component',
  template: `
    <login-component
      *ngIf="mode == 'login'"
      (finish)="startApp()"
    ></login-component>

    <main-form-component
      *ngIf="mode == 'main-form'"
      (exit)="exit()"
    ></main-form-component>
    
    <div *ngIf="mode == 'init'">
      Инициация системы... <span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span>
    </div>
  `
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
