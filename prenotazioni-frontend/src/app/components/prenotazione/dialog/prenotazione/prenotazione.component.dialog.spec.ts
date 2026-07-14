import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrenotazioneComponentDialog } from './prenotazione.component.dialog';

describe('PrenotazioneComponentDialog', () => {
  let component: PrenotazioneComponentDialog;
  let fixture: ComponentFixture<PrenotazioneComponentDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PrenotazioneComponentDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(PrenotazioneComponentDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
