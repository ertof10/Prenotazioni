import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalendarioComponentDialog } from './calendario.component.dialog';

describe('CalendarioComponentDialog', () => {
  let component: CalendarioComponentDialog;
  let fixture: ComponentFixture<CalendarioComponentDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CalendarioComponentDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(CalendarioComponentDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
