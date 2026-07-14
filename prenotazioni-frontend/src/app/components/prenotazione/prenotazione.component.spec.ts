import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrenotazioneComponent } from './prenotazione.component';

describe('PrenotazioneComponent', () => {
  let component: PrenotazioneComponent;
  let fixture: ComponentFixture<PrenotazioneComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PrenotazioneComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PrenotazioneComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
