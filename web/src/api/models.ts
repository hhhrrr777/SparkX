import axios from 'axios';

export interface model {
  id: number;
  name: string;
  status: number,
  logo: string
}

export interface modelListData {
  code: number,
  data: model[],
  msg: string
}

export function list() {
  return axios.get<modelListData>('/models/list');
}

export function getInfo() {
  return axios.get<any>('/models/getInfo');
}
