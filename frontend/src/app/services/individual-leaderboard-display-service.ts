import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, startWith } from 'rxjs/operators';
import { RiderContribution } from '../dto/rider/rider-contribution';
import { LeaderboardService } from './leaderboard-service';
import {
  DUMMY_INDIVIDUAL_ROWS,
  IndividualDisplayRow,
  mergeDummyWithMyRider,
  normalizeRiderContribution,
  readStoredMyRider,
} from '../utils/individual-leaderboard-display';

@Injectable({
  providedIn: 'root',
})
export class IndividualLeaderboardDisplayService {
  constructor(private leaderboardService: LeaderboardService) {}

  loadDisplayIndividuals(includeMe: boolean): Observable<IndividualDisplayRow[]> {
    if (!includeMe) {
      return of([...DUMMY_INDIVIDUAL_ROWS]);
    }

    const storedRider = readStoredMyRider();
    const initialRows = mergeDummyWithMyRider(DUMMY_INDIVIDUAL_ROWS, storedRider);

    return this.fetchMyRider().pipe(
      map((rider) => mergeDummyWithMyRider(DUMMY_INDIVIDUAL_ROWS, rider)),
      startWith(initialRows),
      catchError(() => of(initialRows)),
    );
  }

  getDefaultRiderName(): string {
    return sessionStorage.getItem('currentRiderName') ?? 'Ralitsa';
  }

  private fetchMyRider(): Observable<IndividualDisplayRow | null> {
    const riderId = sessionStorage.getItem('currentRiderId');
    const riderName = sessionStorage.getItem('currentRiderName');

    if (!riderId && !riderName) {
      return of(null);
    }

    return this.leaderboardService.getRiderDetailsLeaderboard().pipe(
      map((riders) => {
        const rider =
          (riderId ? riders.find((entry) => entry.id === riderId) : undefined) ??
          (riderName ? riders.find((entry) => entry.name === riderName) : undefined) ??
          null;

        if (rider) {
          this.persistRider(rider);
          return normalizeRiderContribution(rider);
        }

        return readStoredMyRider();
      }),
      catchError(() => of(readStoredMyRider())),
    );
  }

  private persistRider(rider: RiderContribution): void {
    sessionStorage.setItem('currentRiderName', rider.name);

    if (rider.id) {
      sessionStorage.setItem('currentRiderId', rider.id);
    }
  }
}
