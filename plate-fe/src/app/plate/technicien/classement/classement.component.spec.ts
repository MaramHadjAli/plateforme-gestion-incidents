import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ClassementComponent } from './classement.component';

describe('ClassementComponent', () => {
  let component: ClassementComponent;
  let fixture: ComponentFixture<ClassementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClassementComponent, HttpClientTestingModule, RouterTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClassementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
