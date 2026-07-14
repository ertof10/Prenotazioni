import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServizioComponentDialog } from './servizio.component.dialog';

describe('ServizioComponentDialog', () => {
  let component: ServizioComponentDialog;
  let fixture: ComponentFixture<ServizioComponentDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ServizioComponentDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(ServizioComponentDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
