import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { Home } from './home';
import { TeamLeaderboardDisplayService } from '../services/team-leaderboard-display-service';
import { DUMMY_TEAM_ROWS } from '../utils/team-leaderboard-display';

describe('Home', () => {
  let component: Home;
  let fixture: ComponentFixture<Home>;
  let displayService: jasmine.SpyObj<TeamLeaderboardDisplayService>;

  beforeEach(async () => {
    displayService = jasmine.createSpyObj('TeamLeaderboardDisplayService', ['loadDisplayTeams']);
    displayService.loadDisplayTeams.and.returnValue(of([...DUMMY_TEAM_ROWS]));

    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [{ provide: TeamLeaderboardDisplayService, useValue: displayService }],
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('uses the same display service logic as the full leaderboard', () => {
    component.ngOnInit();
    expect(displayService.loadDisplayTeams).toHaveBeenCalledWith(true, 'full');
  });

  it('defaults to the digital forest tab', () => {
    expect(component.activeTab).toBe('forest');
  });

  it('opens and closes the torch concept overlay', () => {
    expect(component.torchOverlayOpen).toBeFalsy();
    component.openTorchOverlay();
    expect(component.torchOverlayOpen).toBeTruthy();
    expect(component.lightOverlayOpen).toBeFalsy();
    component.closeTorchOverlay();
    expect(component.torchOverlayOpen).toBeFalsy();
  });

  it('opens and closes the light info overlay', () => {
    expect(component.lightOverlayOpen).toBeFalsy();
    component.openLightOverlay();
    expect(component.lightOverlayOpen).toBeTruthy();
    expect(component.torchOverlayOpen).toBeFalsy();
    component.closeLightOverlay();
    expect(component.lightOverlayOpen).toBeFalsy();
  });
});
