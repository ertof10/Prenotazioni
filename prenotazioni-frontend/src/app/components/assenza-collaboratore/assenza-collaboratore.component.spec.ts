import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { AssenzaCollaboratoreComponent } from './assenza-collaboratore.component';

describe('AssenzaCollaboratoreComponent', () => {
  let component: AssenzaCollaboratoreComponent;
  let fixture: ComponentFixture<AssenzaCollaboratoreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AssenzaCollaboratoreComponent],
      imports: [
        HttpClientTestingModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AssenzaCollaboratoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});