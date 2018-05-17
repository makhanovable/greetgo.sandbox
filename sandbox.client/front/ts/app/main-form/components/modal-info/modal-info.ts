import {Component, Inject} from "@angular/core";
import {Charm} from "../../../../model/Charm";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material";

@Component({
  selector: 'modal-info-component',
  template: require('./modal-info.html'),
})
export class ModalInfoComponent {

  c1 = new Charm();
  charmSelectedValue = this.c1;
  charmList: Charm[] | null = null;

  birthDate: any;

  constructor(public dialog: MatDialog) {
    this.c1.id = 1;
    this.c1.name = "Charm 1";
    this.charmList = [];
    this.charmList.push(this.c1);
    this.birthDate = new Date(2015, 1, 4);
  }

  set humanDate(e){
    e = e.split('-');
    let d = new Date(Date.UTC(e[0], e[1]-1, e[2]));
    this.birthDate.setFullYear(d.getUTCFullYear(), d.getUTCMonth(), d.getUTCDate());
  }

  get humanDate(){
    return this.birthDate.toISOString().substring(0, 10);
  }


  checkIt() {
    console.log(this.charmSelectedValue);
  }
}
