import {ClientListComponent} from "./components/clientList/client_list.component";
import {ClientFormComponent} from "./components/clientForm/client_form.component";
import {NgModule} from "@angular/core";
import {HttpModule, JsonpModule} from "@angular/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {RootComponent} from "./root.component";
import {LoginComponent} from "./input/login.component";
import {MainFormComponent} from "./main_form/main_form.component";
import {HttpService} from "./HttpService";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {
  MatButtonModule,
  MatCheckboxModule,
  MatDatepickerModule,
  MatDialogModule,
  MatExpansionModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule,
  MatNativeDateModule,
  MatPaginatorModule,
  MatSelectModule,
  MatSortModule,
  MatToolbarModule
} from "@angular/material";
import {MatTableModule} from "@angular/material/table";
import {TextMaskModule} from "angular2-text-mask";

@NgModule({
  imports: [
    MatTableModule, MatPaginatorModule, TextMaskModule, MatIconModule, MatToolbarModule, MatExpansionModule,
    BrowserModule, HttpModule, JsonpModule, MatDatepickerModule, MatNativeDateModule,
    FormsModule, ReactiveFormsModule, BrowserAnimationsModule, MatSelectModule,
    MatButtonModule, MatCheckboxModule, MatDialogModule, MatSortModule, MatFormFieldModule, MatInputModule
  ],
  declarations: [
    RootComponent, LoginComponent, MainFormComponent, ClientFormComponent, ClientListComponent
  ],
  bootstrap: [RootComponent],
  providers: [HttpService],
  entryComponents: [ClientFormComponent],
})
export class AppModule {
}