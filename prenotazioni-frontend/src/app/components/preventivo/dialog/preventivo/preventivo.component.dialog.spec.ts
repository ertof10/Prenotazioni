import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreventivoComponentDialog } from './preventivo.component.dialog';

describe('PreventivoComponentDialog', () => {
  let component: PreventivoComponentDialog;
  let fixture: ComponentFixture<PreventivoComponentDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PreventivoComponentDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(PreventivoComponentDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
