import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreventivoComponent } from './preventivo.component';

describe('PreventivoComponent', () => {
  let component: PreventivoComponent;
  let fixture: ComponentFixture<PreventivoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PreventivoComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PreventivoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
