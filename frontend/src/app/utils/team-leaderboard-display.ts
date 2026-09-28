import { TeamLeaderboard } from '../dto/team/team-leaderboard';

export interface TeamDisplayRow {
  name: string;
  distanceKm: number;
  points: number;
}

type TeamLike = Partial<TeamLeaderboard> & {
  id?: string;
  name?: string;
};

/** Figma dummy teams – best at top, kept on the leaderboard UI */
export const DUMMY_TEAM_ROWS: TeamDisplayRow[] = [
  { name: 'Team Thunders', distanceKm: 12540, points: 320 },
  { name: 'Team Rockets', distanceKm: 11200, points: 280 },
  { name: 'Team Nile', distanceKm: 9310, points: 250 },
  { name: 'Team BMX', distanceKm: 8420, points: 230 },
  { name: 'Team HotWheels', distanceKm: 7890, points: 200 },
  { name: 'Team CHAD', distanceKm: 7250, points: 170 },
  { name: 'Team Kaya', distanceKm: 6040, points: 160 },
];

export const HOME_PREVIEW_DUMMY_COUNT = 6;

export function normalizeTeamLeaderboard(team: TeamLike | null | undefined): TeamLeaderboard | null {
  if (!team) {
    return null;
  }

  const teamName = (team.teamName ?? team.name ?? '').trim();
  if (!teamName) {
    return null;
  }

  const teamId = (team.teamId ?? team.id ?? '').trim();

  return {
    teamId,
    teamName,
    totalPoints: team.totalPoints ?? 0,
    totalDistance: team.totalDistance ?? 0,
  };
}

export function toDisplayRow(
  team: Pick<TeamLeaderboard, 'teamName' | 'totalPoints' | 'totalDistance'>,
): TeamDisplayRow {
  return {
    name: team.teamName,
    distanceKm: Math.round(team.totalDistance ?? 0),
    points: Math.round(team.totalPoints ?? 0),
  };
}

export function toLeaderboardRow(row: TeamDisplayRow, teamId = ''): TeamLeaderboard {
  return {
    teamId,
    teamName: row.name,
    totalPoints: row.points,
    totalDistance: row.distanceKm,
  };
}

export function mergeDummyWithMyTeam(
  dummyTeams: TeamDisplayRow[],
  myTeam: TeamLike | null | undefined,
): TeamDisplayRow[] {
  const normalizedTeam = normalizeTeamLeaderboard(myTeam);
  if (!normalizedTeam) {
    return [...dummyTeams];
  }

  const rows = normalizedTeam.teamId
    ? dummyTeams.filter((team) => team.name !== normalizedTeam.teamName)
    : [...dummyTeams];

  if (!normalizedTeam.teamId && rows.some((team) => team.name === normalizedTeam.teamName)) {
    return rows;
  }

  rows.push(toDisplayRow(normalizedTeam));
  return rows;
}

export function buildLoggedInTeamRows(
  myTeam: TeamLike | null | undefined,
  dummyTeams: TeamDisplayRow[] = DUMMY_TEAM_ROWS,
): TeamDisplayRow[] {
  return mergeDummyWithMyTeam(dummyTeams, myTeam);
}

/** Home widget: keep room for the user's team row in the fixed-height card */
export function buildHomePreviewRows(
  myTeam: TeamLike | null | undefined,
): TeamDisplayRow[] {
  return mergeDummyWithMyTeam(DUMMY_TEAM_ROWS.slice(0, HOME_PREVIEW_DUMMY_COUNT), myTeam);
}

export function readStoredMyTeam(): TeamLeaderboard | null {
  const teamName = sessionStorage.getItem('currentTeamName');
  if (!teamName) {
    return null;
  }

  return normalizeTeamLeaderboard({
    teamId: sessionStorage.getItem('currentTeamId') ?? '',
    teamName,
    totalPoints: 0,
    totalDistance: 0,
  });
}
