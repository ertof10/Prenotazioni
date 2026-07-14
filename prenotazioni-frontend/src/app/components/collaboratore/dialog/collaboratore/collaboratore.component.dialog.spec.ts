import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollaboratoreComponentDialog } from './collaboratore.component.dialog';

describe('CollaboratoreComponentDialog', () => {
  let component: CollaboratoreComponentDialog;
  let fixture: ComponentFixture<CollaboratoreComponentDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CollaboratoreComponentDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(CollaboratoreComponentDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
