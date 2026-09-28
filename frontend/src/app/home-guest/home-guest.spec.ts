import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { HomeGuest } from './home-guest';
import { TeamLeaderboardDisplayService } from '../services/team-leaderboard-display-service';
import { DUMMY_TEAM_ROWS } from '../utils/team-leaderboard-display';

describe('HomeGuest', () => {
  let component: HomeGuest;
  let fixture: ComponentFixture<HomeGuest>;
  let displayService: jasmine.SpyObj<TeamLeaderboardDisplayService>;

  beforeEach(async () => {
    displayService = jasmine.createSpyObj('TeamLeaderboardDisplayService', ['loadDisplayTeams']);
    displayService.loadDisplayTeams.and.returnValue(of([...DUMMY_TEAM_ROWS]));

    await TestBed.configureTestingModule({
      imports: [HomeGuest],
      providers: [{ provide: TeamLeaderboardDisplayService, useValue: displayService }],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeGuest);
    component = fixture.componentInstance;
    component.ngOnInit();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('loads guest preview teams', () => {
    expect(displayService.loadDisplayTeams).toHaveBeenCalledWith(false, 'full');
    expect(component.displayTeams[0].name).toBe('Team Thunders');
  });

  it('defaults to the digital forest tab', () => {
    expect(component.activeTab).toBe('forest');
  });

  it('routes CTAs to landing for guests', () => {
    expect(component.ctaRoute).toBe('/landing');
  });
});
