import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TicketResponseComponent } from './ticket-response.component';

describe('TicketResponseComponent', () => {
  let component: TicketResponseComponent;
  let fixture: ComponentFixture<TicketResponseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketResponseComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ id: 'TICK-1' }),
              queryParamMap: convertToParamMap({ interest: 'true' })
            }
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
