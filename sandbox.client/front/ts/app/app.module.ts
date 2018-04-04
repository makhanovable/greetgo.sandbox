import {NgModule} from "@angular/core";
import {HttpModule, JsonpModule} from "@angular/http";
import {FormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {RootComponent} from "./root.component";
import {LoginComponent} from "./input/login.component";
import {MainFormComponent} from "./client_form/clients_list";
import {ModalViewComponent} from "./client_form/client_edit_form";
import {HttpService} from "./HttpService";

@NgModule({
  imports: [
    BrowserModule, HttpModule, JsonpModule, FormsModule
  ],
  declarations: [
    RootComponent, LoginComponent, MainFormComponent, ModalViewComponent
  ],
  bootstrap: [RootComponent],
  providers: [HttpService],
  entryComponents: [],
})
export class AppModule {
}