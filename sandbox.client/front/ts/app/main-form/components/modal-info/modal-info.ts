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
  accountInfo: AccountInfo;

  description: string;

  charms = ["charm1", "charm2", "ccharm3"];

  constructor(
    private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialogRef: MatDialogRef<MainFormComponent>) {

    this.accountInfo = data.accountInfo;
    this.description = "some desc"
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: [this.description],
      surname: [this.description],
      patronymic: [this.description],
      gender: ['MALE'],
      charm: [this.charms[1]],
      streetFact: [this.description],
      houseFact: [this.description],
      flatFact: [this.description],
      streetReg: [this.description],
      houseReg: [this.description],
      flatReg: [this.description],
      phoneHome: [this.description],
      phoneWork: [this.description],
      phoneMobile1: [this.description],
      phoneMobile2: [this.description],
      phoneMobile3: [this.description],
    });
  }


  save() {
    this.dialogRef.close(this.form.value);
  }

  close() {
    this.dialogRef.close();
  }
}
