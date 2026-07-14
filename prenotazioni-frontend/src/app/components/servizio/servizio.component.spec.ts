import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServizioComponent } from './servizio.component';

describe('ServizioComponent', () => {
  let component: ServizioComponent;
  let fixture: ComponentFixture<ServizioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ServizioComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ServizioComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
