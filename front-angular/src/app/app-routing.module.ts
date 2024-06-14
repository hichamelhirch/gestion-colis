import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {CreateColisIndividualComponent} from "./create-colis-individual/create-colis-individual.component";
import {CreateColisBulkComponent} from "./create-colis-bulk/create-colis-bulk.component";
import {CreateClientsComponent} from "./create-clients/create-clients.component";
import {ConfirmationDialogComponent} from "./confirmation-dialog/confirmation-dialog.component";
import {LoginComponent} from "./login/login.component";
import {StockColisComponent} from "./stock-colis/stock-colis.component";
import {AuthGuard} from "./guards/auth.guard";

import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {TokenInterceptor} from "./token.interceptor";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ColisDetailsComponent} from "./colis-details/colis-details.component";
import {EditColisComponent} from "./edit-colis/edit-colis.component";
import {ProfileComponent} from "./profile/profile.component";
import {EmailComposerComponent} from "./email-composer/email-composer.component";
import {FilterDialogComponent} from "./filter-dialog/filter-dialog.component";

const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'create-client', component: CreateClientsComponent },
      { path: 'create-colis-bulk', component: CreateColisBulkComponent },
      { path: 'create-colis-individual', component: CreateColisIndividualComponent },
      { path: 'confirmer', component: ConfirmationDialogComponent },
      { path: 'colislist', component: StockColisComponent },
      { path: 'edit-colis/:id', component: EditColisComponent },
      { path: 'colis-details/:id', component: ColisDetailsComponent },
      { path: 'email-composer', component: EmailComposerComponent },
      { path: 'profile', component: ProfileComponent },
      {path:'filter',component:FilterDialogComponent},
      { path: '', redirectTo: '/home', pathMatch: 'full' },
    ]
  },
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];

providers: [
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
  { provide: MAT_DIALOG_DATA, useValue: {} },
  { provide: MatDialogRef, useValue: {} },

]
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
