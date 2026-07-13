import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing-module';

import { App } from './app';

import { HomeComponent } from './components/home/home.component';
import { Login } from './components/login/login.component';

import { AreaRiservataComponent } from './components/area-riservata/area-riservata.component';

import { HeaderComponent } from './components/layout/header/header.component';
import { SidebarComponent } from './components/layout/sidebar/sidebar.component';

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

import { AuthInterceptor } from './security/auth.interceptor';

@NgModule({
  declarations: [
    App,

    HomeComponent,
    Login,

    AreaRiservataComponent,

    HeaderComponent,
    SidebarComponent,

    Dashboard,

    UtenteComponent,
    ServizioComponent,
    CollaboratoreComponent,
    CollaboratoreServizioComponent,
    PrenotazioneComponent,
    PreventivoComponent,
    CalendarioComponent,
    AssenzaCollaboratoreComponent,
    ImpostazioniAttivitaComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [
    App
  ]
})
export class AppModule { }