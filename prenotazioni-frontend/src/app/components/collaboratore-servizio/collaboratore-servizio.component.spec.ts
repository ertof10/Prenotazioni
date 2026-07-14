import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { CollaboratoreServizioComponent } from './collaboratore-servizio.component';

describe('CollaboratoreServizioComponent', () => {
  let component: CollaboratoreServizioComponent;
  let fixture: ComponentFixture<CollaboratoreServizioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CollaboratoreServizioComponent],
      imports: [
        HttpClientTestingModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CollaboratoreServizioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});