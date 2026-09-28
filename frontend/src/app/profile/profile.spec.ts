import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';

import { Profile } from './profile';
import { RiderService } from '../services/rider-service';
import { TeamService } from '../services/team-service';

describe('Profile', () => {
  let component: Profile;
  let fixture: ComponentFixture<Profile>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Profile],
      providers: [
        {
          provide: RiderService,
          useValue: {
            getRider: () =>
              of({
                id: 'rider-1',
                name: 'student@tuwien.ac.at',
                gender: 'M',
              }),
          },
        },
        {
          provide: TeamService,
          useValue: {
            getTeamForMember: () =>
              of({
                teamId: 'team-1',
                teamName: 'My Team',
                totalPoints: 0,
                totalDistance: 0,
              }),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Profile);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('clears session and navigates to home-guest on logout', () => {
    const navigateByUrlSpy = spyOn(router, 'navigateByUrl');
    spyOn(sessionStorage, 'removeItem');

    component.logOut();

    expect(sessionStorage.removeItem).toHaveBeenCalledWith('currentRiderId');
    expect(sessionStorage.removeItem).toHaveBeenCalledWith('currentRiderName');
    expect(sessionStorage.removeItem).toHaveBeenCalledWith('currentTeamId');
    expect(sessionStorage.removeItem).toHaveBeenCalledWith('currentTeamName');
    expect(navigateByUrlSpy).toHaveBeenCalledWith('/home-guest', { replaceUrl: true });
  });
});
