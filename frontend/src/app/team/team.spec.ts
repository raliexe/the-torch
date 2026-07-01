import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { Team } from './team';
import { RiderService } from '../services/rider-service';
import { TeamService } from '../services/team-service';
import { LeaderboardService } from '../services/leaderboard-service';

describe('Team', () => {
  let component: Team;
  let fixture: ComponentFixture<Team>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Team],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: RiderService,
          useValue: {
            getRider: () => of({ id: 'rider-1', name: 'Steve' }),
          },
        },
        {
          provide: TeamService,
          useValue: {
            getTeamForMember: () =>
              of({
                teamId: 'team-1',
                teamName: 'Team Rockets',
                totalPoints: 1250,
                totalDistance: 0,
              }),
          },
        },
        {
          provide: LeaderboardService,
          useValue: {
            getTeamDetailsLeaderboard: () => of([]),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Team);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
