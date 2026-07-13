import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomeComponent } from './components/home/home.component';
import { Login } from './components/login/login.component';

import { AreaRiservataComponent } from './components/area-riservata/area-riservata.component';

import { Dashboard } from './components/dashboard/dashboard.component';
import { UtenteComponent } from './components/utente/utente.component';
import { ServizioComponent } from './components/servizio/servizio.component';
import { CollaboratoreComponent } from './components/collaboratore/collaboratore.component';
import { CollaboratoreServizioComponent } from './components/collaboratore-servizio/collaboratore-servizio.component';
import { PrenotazioneComponent } from './components/prenotazione/prenotazione.component';
import { PreventivoComponent } from './components/preventivo/preventivo.component';
import { CalendarioComponent } from './components/calendario/calendario.component';
import { AssenzaCollaboratoreComponent } from './components/assenza-collaboratore/assenza-collaboratore.component';
import { ImpostazioniAttivitaComponent } from './components/impostazioni-attivita/impostazioni-attivita.component';

import { AuthGuard } from './security/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'login',
    component: Login
  },
  {
    path: '',
    component: AreaRiservataComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        component: Dashboard
      },
      {
        path: 'utenti',
        component: UtenteComponent
      },
      {
        path: 'servizi',
        component: ServizioComponent
      },
      {
        path: 'collaboratori',
        component: CollaboratoreComponent
      },
      {
        path: 'collaboratore-servizio',
        component: CollaboratoreServizioComponent
      },
      {
        path: 'prenotazioni',
        component: PrenotazioneComponent
      },
      {
        path: 'preventivi',
        component: PreventivoComponent
      },
      {
        path: 'calendario',
        component: CalendarioComponent
      },
      {
        path: 'assenze',
        component: AssenzaCollaboratoreComponent
      },
      {
        path: 'impostazioni-attivita',
        component: ImpostazioniAttivitaComponent
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule { }