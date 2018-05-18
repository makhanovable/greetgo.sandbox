import {Component, Inject, OnInit} from "@angular/core";
import {Charm} from "../../../../model/Charm";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material";
import {AccountInfo} from "../../../../model/AccountInfo";
import {MainFormComponent} from "../../main-form";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: 'modal-info-component',
  template: require('./modal-info.html'),
  styles: [require('./modal-info.css')],
})
export class ModalInfoComponent implements OnInit {

  form: FormGroup;
  accountInfo: AccountInfo = null;

  name: string = '';
  surname: string = '';
  patronymic: string = '';
  gender: 'MALE';
  charm: string[] = [];
  streetFact: string = '';
  houseFact: string = '';
  flatFact: string = '';
  streetReg: string = '';
  houseReg: string = '';
  flatReg: string = '';
  phoneHome: string = '';
  phoneWork: string = '';
  phoneMobile1: string = '';
  phoneMobile2: string = '';
  phoneMobile3: string = '';

  charms = [];

  constructor(
    private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialogRef: MatDialogRef<MainFormComponent>) {

    if(typeof data.accountInfo != 'undefined')
      this.accountInfo = data.accountInfo;
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: [this.name],
      surname: [this.surname],
      patronymic: [this.patronymic],
      gender: ['MALE'],
      charm: [this.charms[1]],
      streetFact: [this.streetFact],
      houseFact: [this.houseFact],
      flatFact: [this.flatFact],
      streetReg: [this.streetReg],
      houseReg: [this.houseReg],
      flatReg: [this.flatReg],
      phoneHome: [this.phoneHome],
      phoneWork: [this.phoneWork],
      phoneMobile1: [this.phoneMobile1],
      phoneMobile2: [this.phoneMobile2],
      phoneMobile3: [this.phoneMobile3],
    });
  }


  save() {
    this.dialogRef.close(this.form.value);
  }

  close() {
    this.dialogRef.close();
  }
}
