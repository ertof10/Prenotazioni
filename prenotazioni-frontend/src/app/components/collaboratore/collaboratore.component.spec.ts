import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollaboratoreComponent } from './collaboratore.component';

describe('CollaboratoreComponent', () => {
  let component: CollaboratoreComponent;
  let fixture: ComponentFixture<CollaboratoreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CollaboratoreComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CollaboratoreComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
