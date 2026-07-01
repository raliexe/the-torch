import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { TeamService } from '../services/team-service';
import { persistTeamSession } from './team-session';

/** Allows /join-team only when the rider is not already on a team. */
export const noTeamGuard: CanActivateFn = () => {
  const router = inject(Router);
  const teamService = inject(TeamService);
  const riderId = sessionStorage.getItem('currentRiderId');

  if (!riderId) {
    return router.createUrlTree(['/home-guest']);
  }

  return teamService.getTeamForMember(riderId).pipe(
    map((team) => {
      persistTeamSession(team);
      return router.createUrlTree(['/home']);
    }),
    catchError((err) => {
      if (err.status === 404) {
        return of(true);
      }

      return of(router.createUrlTree(['/home']));
    }),
  );
};
