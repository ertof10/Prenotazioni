import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollaboratoreServizioComponentDialog } from './collaboratore-servizio.component.dialog';

describe('CollaboratoreServizioComponentDialog', () => {
  let component: CollaboratoreServizioComponentDialog;
  let fixture: ComponentFixture<CollaboratoreServizioComponentDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CollaboratoreServizioComponentDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(CollaboratoreServizioComponentDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
