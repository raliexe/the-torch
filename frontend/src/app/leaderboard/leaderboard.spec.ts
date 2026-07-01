import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { Leaderboard } from './leaderboard';
import { TeamLeaderboardDisplayService } from '../services/team-leaderboard-display-service';
import { DUMMY_TEAM_ROWS } from '../utils/team-leaderboard-display';

describe('Leaderboard', () => {
  let component: Leaderboard;
  let fixture: ComponentFixture<Leaderboard>;
  let displayService: jasmine.SpyObj<TeamLeaderboardDisplayService>;

  beforeEach(async () => {
    displayService = jasmine.createSpyObj('TeamLeaderboardDisplayService', [
      'loadDisplayTeams',
      'getDefaultMyTeamName',
    ]);
    displayService.loadDisplayTeams.and.returnValue(of([...DUMMY_TEAM_ROWS]));
    displayService.getDefaultMyTeamName.and.returnValue('Team Rockets');

    await TestBed.configureTestingModule({
      imports: [Leaderboard],
      providers: [{ provide: TeamLeaderboardDisplayService, useValue: displayService }],
    }).compileComponents();

    fixture = TestBed.createComponent(Leaderboard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('loads display teams through the shared display service', () => {
    const teams = [
      ...DUMMY_TEAM_ROWS,
      { name: 'My New Team', distanceKm: 0, points: 0 },
    ];
    displayService.loadDisplayTeams.and.returnValue(of(teams));
    displayService.getDefaultMyTeamName.and.returnValue('My New Team');

    component.ngOnInit();

    expect(displayService.loadDisplayTeams).toHaveBeenCalledWith(true, 'full');
    expect(component.displayTeams.at(-1)?.name).toBe('My New Team');
    expect(component.myTeamName).toBe('My New Team');
  });

  it('keeps dummy teams when the shared service fails', () => {
    displayService.loadDisplayTeams.and.returnValue(throwError(() => new Error('failed')));

    component.ngOnInit();

    expect(component.displayTeams[0].name).toBe('Team Thunders');
  });
});
