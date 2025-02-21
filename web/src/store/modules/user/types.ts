export type RoleType = '' | '*' | 'admin' | 'user';
export interface UserState {
  id?: number,
  nickname?: string;
  role: RoleType;
}
