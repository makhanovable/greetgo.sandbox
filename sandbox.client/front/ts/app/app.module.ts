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
import {ClientListComponent} from "./client.list/client.list.component";
import {MatDialogModule} from "@angular/material";
import {DialogComponent} from "./edit.dialog/edit.dialog.component";
import {ErrorDialogComponent} from "./error.dialog/error.dialog.component";
import {HttpClientModule} from '@angular/common/http';
import {DeleteDialogComponent} from "./delete.dialog/delete.dialog.component";
import {DataSourceService} from "./services/data.source.service";

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
        RootComponent, LoginComponent, MainFormComponent, ClientListComponent,
        DialogComponent, ErrorDialogComponent, DeleteDialogComponent,
    ],
    bootstrap: [RootComponent],
    providers: [HttpService, DataSourceService],
    entryComponents: [DialogComponent, ErrorDialogComponent, DeleteDialogComponent],
})
export class AppModule {
}