
import { Component, EventEmitter, Output } from "@angular/core";
import { UserInfo } from "../../model/UserInfo";
import { HttpService } from "../HttpService";
import { MatTableDataSource, MatPaginator, MatDialog } from "@angular/material";
import { ViewChild } from "@angular/core/src/metadata/di";

import { OnInit } from '@angular/core/src/metadata/lifecycle_hooks';
import { SelectionModel } from '@angular/cdk/collections';
import { ClientFormComponent } from '../components/clientForm/client_form.component';

@Component({
  selector: 'main-form-component',
  template: require('./main_form.component.html'),
  styles: [require('./main_form.component.css')],
})
export class MainFormComponent implements OnInit {
  
  @Output() exit = new EventEmitter<void>();

  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;


  
  

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

  ngOnInit() {


  }

  pong() {
    console.log('pong')
  }

  

  





}

