import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { LeaderboardIndividuals } from './leaderboard-individuals';
import { IndividualLeaderboardDisplayService } from '../services/individual-leaderboard-display-service';
import { TeamLeaderboardDisplayService } from '../services/team-leaderboard-display-service';
import { DUMMY_INDIVIDUAL_ROWS } from '../utils/individual-leaderboard-display';

describe('LeaderboardIndividuals', () => {
  let component: LeaderboardIndividuals;
  let fixture: ComponentFixture<LeaderboardIndividuals>;
  let displayService: jasmine.SpyObj<IndividualLeaderboardDisplayService>;

  beforeEach(async () => {
    displayService = jasmine.createSpyObj('IndividualLeaderboardDisplayService', [
      'loadDisplayIndividuals',
    ]);
    displayService.loadDisplayIndividuals.and.returnValue(of([...DUMMY_INDIVIDUAL_ROWS]));

    await TestBed.configureTestingModule({
      imports: [LeaderboardIndividuals],
      providers: [
        { provide: IndividualLeaderboardDisplayService, useValue: displayService },
        {
          provide: TeamLeaderboardDisplayService,
          useValue: {
            getDefaultMyTeamName: () => 'Team Rockets',
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LeaderboardIndividuals);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('loads display individuals through the shared display service', () => {
    const individuals = [
      ...DUMMY_INDIVIDUAL_ROWS,
      { id: 'rider-1', name: 'student@tuwien.ac.at', distanceKm: 0, points: 0 },
    ];
    displayService.loadDisplayIndividuals.and.returnValue(of(individuals));

    component.ngOnInit();

    expect(displayService.loadDisplayIndividuals).toHaveBeenCalledWith(true);
    expect(component.displayIndividuals.at(-1)?.name).toBe('student@tuwien.ac.at');
  });
});
