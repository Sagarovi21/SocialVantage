import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SocialvrComponent } from './socialvr.component';

describe('SocialvrComponent', () => {
  let component: SocialvrComponent;
  let fixture: ComponentFixture<SocialvrComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SocialvrComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SocialvrComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
