import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssenzaCollaboratoreComponentDialog } from './assenza-collaboratore.component.dialog';

describe('AssenzaCollaboratoreComponentDialog', () => {
  let component: AssenzaCollaboratoreComponentDialog;
  let fixture: ComponentFixture<AssenzaCollaboratoreComponentDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AssenzaCollaboratoreComponentDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(AssenzaCollaboratoreComponentDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
