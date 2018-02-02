import { NgModule } from "@angular/core";
import { HttpModule, JsonpModule } from "@angular/http";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { RootComponent } from "./root.component";
import { LoginComponent } from "./input/login.component";
import { MainFormComponent } from "./main_form/main_form.component";
import { HttpService } from "./HttpService";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule, MatPaginatorModule } from '@angular/material';
import { MatTableModule } from '@angular/material/table';

@NgModule({
  imports: [
    MatTableModule, MatPaginatorModule,
    BrowserModule, HttpModule, JsonpModule, 
    FormsModule, ReactiveFormsModule, BrowserAnimationsModule, 
    MatButtonModule
  ],
  declarations: [
    RootComponent, LoginComponent, MainFormComponent
  ],
  bootstrap: [RootComponent],
  providers: [HttpService],
  entryComponents: [],
})
export class AppModule {
}