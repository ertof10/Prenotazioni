import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImpostazioniAttivitaComponent } from './impostazioni-attivita.component';

describe('ImpostazioniAttivitaComponent', () => {
  let component: ImpostazioniAttivitaComponent;
  let fixture: ComponentFixture<ImpostazioniAttivitaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ImpostazioniAttivitaComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ImpostazioniAttivitaComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
