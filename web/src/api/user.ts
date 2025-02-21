import axios from 'axios';
import { UserState } from '@/store/modules/user/types';

export interface LoginData {
  account: string;
  password: string;
}

export interface LoginRes {
  nickname: string | undefined;
  id: number | undefined;
  token: string;
}

export interface UserPwd {
  password: string | undefined;
  rePassword: string | undefined;
}

export function login(data: LoginData) {
  return axios.post<LoginRes>('/login/doLogin', data);
}

export function logout() {
  return axios.post<LoginRes>('/login/logout');
}

export function getUserInfo() {
  return axios.post<UserState>('/users/info');
}

export function resetPwd(data: UserPwd) {
  return axios.post('/users/resetPwd', data);
}
