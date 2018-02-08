import {Component, EventEmitter, Output} from "@angular/core";

@Component({
  selector: 'main-form-component',
  template: require('./main_form.component.html'),
  styles: [require('./main_form.component.css')],
})
export class MainFormComponent {

  @Output() exit = new EventEmitter<void>();

  constructor() {
  }

}

