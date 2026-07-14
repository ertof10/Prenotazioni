import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImpostazioniAttivitaComponentDialog } from './impostazioni-attivita.component.dialog';

describe('ImpostazioniAttivitaComponentDialog', () => {
  let component: ImpostazioniAttivitaComponentDialog;
  let fixture: ComponentFixture<ImpostazioniAttivitaComponentDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ImpostazioniAttivitaComponentDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(ImpostazioniAttivitaComponentDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
