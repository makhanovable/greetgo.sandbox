import {NgModule} from "@angular/core";
import {HttpModule, JsonpModule} from "@angular/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RootComponent} from "./root.component";
import {LoginComponent} from "./input/login.component";
import {MainFormComponent} from "./main_form/main_form.component";
import {HttpService} from "./HttpService";
import {
    MatButtonModule, MatFormFieldModule, MatInputModule, MatSlideToggleModule,
    MatTableModule, MatPaginatorModule, MatSelectModule, MatDatepickerModule, MatNativeDateModule,
    MatSortModule
} from "@angular/material";
import {EditableListComponent} from "./editable_list/editable.list.component";
import {MatDialogModule} from "@angular/material";
import {DialogComponent} from "./dialog/dialog.component";
import {ErrorDialogComponent} from "./error_dialog/error.dialog.component";
import {AreYouSureDialogComponent} from "./are_you_sure_dialog/are.you.sure.dialog.component";

@NgModule({
    imports: [
        BrowserModule, HttpModule, JsonpModule, FormsModule,
        BrowserAnimationsModule,
        MatTableModule,
        MatSlideToggleModule,
        MatDialogModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatPaginatorModule,
        MatSelectModule,
        MatDialogModule,
        MatNativeDateModule,
        MatDatepickerModule,
        MatSortModule,

    ],
    declarations: [
        RootComponent, LoginComponent, MainFormComponent, EditableListComponent,
        DialogComponent, ErrorDialogComponent, AreYouSureDialogComponent
    ],
    bootstrap: [RootComponent],
    providers: [HttpService],
    entryComponents: [DialogComponent, ErrorDialogComponent, AreYouSureDialogComponent],
})
export class AppModule {
}