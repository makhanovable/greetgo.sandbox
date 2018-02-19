import { HttpService } from './../HttpService';
import {Component, EventEmitter, Output} from "@angular/core";

@Component({
  selector: 'main-form-component',
  template: require('./main_form.component.html'),
  styles: [require('./main_form.component.css')],
})
export class MainFormComponent {

  @Output() exit = new EventEmitter<void>();

  constructor(private httpService:HttpService) {
  }

  download(){
    this.httpService.get("/client/report", {
      type:"xlsx"
    }).toPromise().then(data=>{
      
      var blob = new Blob([data], { type: 'text/csv' });
      var url= window.URL.createObjectURL(blob);
      window.open(url);
      
    }).catch(err=>{
      console.log(err);
    })

  }



}

