export class Rider {
  id!: string;
  name!: string;
  gender!: string;
  coefficient?: number;
  tuidToken?: string;
  stravaAccessToken?: string;
  stravaRefreshToken?: string;
  stravaTokenExpiresAt?: number;
}
