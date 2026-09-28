import { RiderContribution } from '../dto/rider/rider-contribution';

export interface IndividualDisplayRow {
  id: string;
  name: string;
  distanceKm: number;
  points: number;
}

type RiderLike = Partial<RiderContribution> & {
  riderId?: string;
  riderName?: string;
  totalDistance?: number;
  totalPoints?: number;
};

/** Figma dummy riders – best at top, kept on the individuals leaderboard UI */
export const DUMMY_INDIVIDUAL_ROWS: IndividualDisplayRow[] = [
  { id: 'dummy-victor', name: 'Victor', distanceKm: 1010, points: 320 },
  { id: 'dummy-ehsan', name: 'Ehsan', distanceKm: 1000, points: 280 },
  { id: 'dummy-simin', name: 'Simin', distanceKm: 910, points: 250 },
  { id: 'dummy-ralitsa', name: 'Ralitsa', distanceKm: 840, points: 230 },
  { id: 'dummy-filip', name: 'Filip', distanceKm: 800, points: 200 },
  { id: 'dummy-sep', name: 'Sep', distanceKm: 725, points: 170 },
  { id: 'dummy-tim', name: 'Tim', distanceKm: 700, points: 160 },
];

export function normalizeRiderContribution(rider: RiderLike | null | undefined): IndividualDisplayRow | null {
  if (!rider) {
    return null;
  }

  const name = (rider.name ?? rider.riderName ?? '').trim();
  const id = (rider.id ?? rider.riderId ?? '').trim();

  if (!name && !id) {
    return null;
  }

  return {
    id,
    name: name || id,
    distanceKm: Math.round(rider.distance ?? rider.totalDistance ?? 0),
    points: Math.round(rider.contributedPoints ?? rider.totalPoints ?? 0),
  };
}

export function mergeDummyWithMyRider(
  dummyRiders: IndividualDisplayRow[],
  myRider: RiderLike | null | undefined,
): IndividualDisplayRow[] {
  const normalizedRider = normalizeRiderContribution(myRider);
  if (!normalizedRider) {
    return [...dummyRiders];
  }

  const rows = normalizedRider.id
    ? dummyRiders.filter(
        (rider) => rider.id !== normalizedRider.id && rider.name !== normalizedRider.name,
      )
    : [...dummyRiders];

  if (
    !normalizedRider.id &&
    rows.some((rider) => rider.name === normalizedRider.name)
  ) {
    return rows;
  }

  rows.push(normalizedRider);
  return rows;
}

export function readStoredMyRider(): IndividualDisplayRow | null {
  const riderName = sessionStorage.getItem('currentRiderName');
  const riderId = sessionStorage.getItem('currentRiderId');

  if (!riderName && !riderId) {
    return null;
  }

  return normalizeRiderContribution({
    id: riderId ?? '',
    name: riderName ?? '',
    distance: 0,
    contributedPoints: 0,
  });
}
