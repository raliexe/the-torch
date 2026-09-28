import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { JoinCreateTeam } from './join-create-team';
import { RiderService } from '../services/rider-service';
import { TeamService } from '../services/team-service';

describe('JoinCreateTeam', () => {
  let component: JoinCreateTeam;
  let fixture: ComponentFixture<JoinCreateTeam>;
  let router: Router;
  let riderService: jasmine.SpyObj<RiderService>;
  let teamService: jasmine.SpyObj<TeamService>;

  beforeEach(async () => {
    riderService = jasmine.createSpyObj('RiderService', ['getRider']);
    teamService = jasmine.createSpyObj('TeamService', ['createTeam', 'getTeamForMember']);

    await TestBed.configureTestingModule({
      imports: [JoinCreateTeam],
      providers: [
        { provide: RiderService, useValue: riderService },
        { provide: TeamService, useValue: teamService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(JoinCreateTeam);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('defaults to create mode', () => {
    expect(component.mode).toBe('create');
  });

  it('switches to create mode', () => {
    component.selectMode('create');
    expect(component.mode).toBe('create');
  });

  it('creates a team and navigates to home', () => {
    const navigateSpy = spyOn(router, 'navigate');
    spyOn(sessionStorage, 'getItem').and.callFake((key: string) =>
      key === 'currentRiderId' ? 'rider-1' : null,
    );
    teamService.createTeam.and.returnValue(
      of({
        id: 'team-1',
        name: 'MY-TEAM',
        coefficient: 1,
      }),
    );
    teamService.getTeamForMember.and.returnValue(
      of({
        teamId: 'team-1',
        teamName: 'MY-TEAM',
        totalPoints: 0,
        totalDistance: 0,
      }),
    );

    component.selectMode('create');
    component.teamCode = 'MY-TEAM';
    component.submit();

    expect(riderService.getRider).not.toHaveBeenCalled();
    expect(teamService.createTeam).toHaveBeenCalledWith({
      name: 'MY-TEAM',
      ownerId: 'rider-1',
    });
    expect(teamService.getTeamForMember).toHaveBeenCalledWith('rider-1');
    expect(navigateSpy).toHaveBeenCalledWith(['/home']);
  });

  it('shows a message when join mode is used', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.teamCode = 'ABC123';
    component.selectMode('join');
    component.submit();
    expect(navigateSpy).not.toHaveBeenCalled();
    expect(component.errorMessage).toContain('Join team is not connected yet');
  });

  it('does not navigate without a code', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.teamCode = '   ';
    component.submit();
    expect(navigateSpy).not.toHaveBeenCalled();

    component.selectMode('create');
    component.submit();
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('shows an error when create team fails', () => {
    spyOn(sessionStorage, 'getItem').and.callFake((key: string) =>
      key === 'currentRiderId' ? 'rider-1' : null,
    );
    teamService.createTeam.and.returnValue(throwError(() => ({ status: 500 })));

    component.selectMode('create');
    component.teamCode = 'MY-TEAM';
    component.submit();

    expect(component.errorMessage).toBe('Could not create the team. Please try again.');
    expect(component.isSubmitting).toBeFalse();
  });
});
