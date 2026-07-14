import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UtenteComponentDialog } from './utente.component.dialog';

describe('UtenteComponentDialog', () => {
  let component: UtenteComponentDialog;
  let fixture: ComponentFixture<UtenteComponentDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UtenteComponentDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(UtenteComponentDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
