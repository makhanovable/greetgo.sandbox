import {Component, EventEmitter, Output} from "@angular/core";
import {HttpService} from "../HttpService";
import "rxjs/add/operator/catch";
import "rxjs/add/operator/map";
import "rxjs/add/operator/toPromise";

@Component({
  selector: 'login-component',
  template: require('./login-component.html'),
  styles: [require('./login-component.css')],
})
export class LoginComponent {
  registration: boolean = false;
  enterButtonEnabled: boolean = false;

  errorMessage: string | null = null;

  disabled: boolean = false;

  fieldEnterLogin: string = '';
  fieldEnterPassword: string = '';

  @Output() finish = new EventEmitter<void>();

  constructor(private httpService: HttpService) {}

  forgotPassword() {
      window.alert("При запуске скрипта инициации БД выводятся пользователи и их пароли: посмотрите там!");
  }

  updateEnterButton() {
    this.enterButtonEnabled = !!this.fieldEnterLogin && !!this.fieldEnterPassword;
    console.log("this.enterButtonEnabled = " + this.enterButtonEnabled)
  }

  enterButtonClicked() {
    this.disabled = true;
    this.enterButtonEnabled = false;

    this.httpService.post("/auth/login", {
      accountName: this.fieldEnterLogin,
      password: this.fieldEnterPassword,
    }).toPromise().then(res => {
      this.disabled = false;
      this.httpService.token = res.text();
      this.errorMessage = null;
      if (this.fieldEnterLogin == 'root') {
        localStorage.removeItem("lastGoodLogin");
      } else {
        localStorage.setItem("lastGoodLogin", this.fieldEnterLogin);
      }
      this.finish.emit();
    }, error => {
      this.disabled = false;
      this.enterButtonEnabled = true;
      console.error("AUTHENTICATION_UNKNOWN_ERROR", error);
      if (400 <= error.status && error.status < 500) {
        this.errorMessage = error.text();
      } else {
        this.errorMessage = error;
      }
    });
  }

  registrationButtonClicked() {
    window.alert("Регистрация ещё не работает");
    this.registration = false;
  }
}