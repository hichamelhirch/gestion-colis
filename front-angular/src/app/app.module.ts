import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';


import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { CreateColisIndividualComponent } from './create-colis-individual/create-colis-individual.component';
import { CreateColisBulkComponent } from './create-colis-bulk/create-colis-bulk.component';
import {MatToolbar, MatToolbarModule} from "@angular/material/toolbar";
import {MatIcon, MatIconModule} from "@angular/material/icon";
import {MatMenu, MatMenuModule, MatMenuTrigger} from "@angular/material/menu";
import {MatDrawerContainer, MatSidenavModule} from "@angular/material/sidenav";
import {MatListItem, MatListModule, MatNavList} from "@angular/material/list";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {MatStepperModule} from "@angular/material/stepper";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import {MatInputModule} from "@angular/material/input";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import { CreateClientsComponent } from './create-clients/create-clients.component';
import {MatCardContent, MatCardModule, MatCardTitle} from "@angular/material/card";
import { StockColisComponent } from './stock-colis/stock-colis.component';
import { ColisDetailsComponent } from './colis-details/colis-details.component';
import { ConfirmationDialogComponent } from './confirmation-dialog/confirmation-dialog.component';
import {MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import { LoginComponent } from './login/login.component';
import {MatAutocomplete, MatAutocompleteModule, MatAutocompleteTrigger} from "@angular/material/autocomplete";
import {MatTable, MatTableModule} from "@angular/material/table";
import { TruncatePipe } from './truncate.pipe';
import { CopierDirective } from './copier.directive';
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSortModule} from "@angular/material/sort";
import {MatExpansionPanelTitle} from "@angular/material/expansion";
import {TokenInterceptor} from "./token.interceptor";
import {MatCheckboxModule} from "@angular/material/checkbox";
import { SearcColisComponent } from './searc-colis/searc-colis.component';
import { SuccessDialogComponent } from './success-dialog/success-dialog.component';
import { ErrorMessagesComponent } from './error-messages/error-messages.component';
import { ColisLabelComponent } from './colis-label/colis-label.component';
import { EditColisComponent } from './edit-colis/edit-colis.component';
import {FlexLayoutModule} from "@angular/flex-layout";
import {AuthService} from "./services/auth.service";
import { ProfileComponent } from './profile/profile.component';
import {AuthGuard} from "./guards/auth.guard";
import { EmailComposerComponent } from './email-composer/email-composer.component';
import { CancelDialogComponent } from './cancel-dialog/cancel-dialog.component';
import { FilterDialogComponent } from './filter-dialog/filter-dialog.component';
import { FilteredColisComponent } from './filtered-colis/filtered-colis.component';
import { SuiviColisComponent } from './suivi-colis/suivi-colis.component';
import { RatingComponent } from './rating/rating.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    CreateColisIndividualComponent,
    CreateColisBulkComponent,
    CreateClientsComponent,
    StockColisComponent,
    ColisDetailsComponent,
    ConfirmationDialogComponent,
    LoginComponent,
    TruncatePipe,
    CopierDirective,
    SearcColisComponent,
    SuccessDialogComponent,
    ErrorMessagesComponent,
    ColisLabelComponent,
    EditColisComponent,
    ProfileComponent,
    EmailComposerComponent,
    CancelDialogComponent,
    FilterDialogComponent,
    FilteredColisComponent,
    SuiviColisComponent,
    RatingComponent
  ],

  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatSidenavModule,
    MatListModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatStepperModule,
    MatFormFieldModule,
    FlexLayoutModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule,
    MatSelectModule,
    MatButtonModule,
    MatSnackBarModule,
    MatFormFieldModule,
    MatCardContent,
    MatCardTitle,
    MatCardModule,
    MatStepperModule,
    MatDialogModule,
    ReactiveFormsModule,
    MatAutocompleteModule,
    MatAutocompleteTrigger,
    MatInputModule,
    MatSelectModule,
    MatAutocompleteModule,
    MatCardModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatAutocompleteModule,
    MatExpansionPanelTitle,
    MatCheckboxModule,
    MatButtonModule,
    MatIconModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,


  ],

  providers: [
    provideAnimationsAsync(),//{ provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
    AuthService,AuthGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    }
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  bootstrap: [AppComponent]
})
export class AppModule { }
