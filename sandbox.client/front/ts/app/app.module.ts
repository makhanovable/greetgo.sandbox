import {NgModule} from "@angular/core";
import {HttpModule, JsonpModule} from "@angular/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RootComponent} from "./root.component";
import {LoginComponent} from "./input/login.component";
import {MainFormComponent} from "./main.form/main.form.component";
import {MatToolbarModule} from '@angular/material/toolbar';
import {HttpService} from "./HttpService";
import {
    MatButtonModule, MatFormFieldModule, MatInputModule, MatSlideToggleModule,
    MatTableModule, MatPaginatorModule, MatSelectModule, MatDatepickerModule, MatNativeDateModule,
    MatProgressSpinnerModule,
    MatSortModule
} from "@angular/material";
import {EditableListComponent} from "./editable.list/editable.list.component";
import {MatDialogModule} from "@angular/material";
import {DialogComponent} from "./dialog/dialog.component";
import {ErrorDialogComponent} from "./error.dialog/error.dialog.component";
import {HttpClientModule} from '@angular/common/http';
import {AreYouSureDialogComponent} from "./are.you.sure.dialog/are.you.sure.dialog.component";

@NgModule({
    imports: [
        BrowserModule, HttpModule, JsonpModule, FormsModule,
        BrowserAnimationsModule,
        MatTableModule,
        MatSlideToggleModule,
        MatDialogModule,
        ReactiveFormsModule,
        MatToolbarModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatPaginatorModule,
        MatSelectModule,
        MatDialogModule,
        MatNativeDateModule,
        MatDatepickerModule,
        MatSortModule,
        MatProgressSpinnerModule,
        HttpClientModule
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