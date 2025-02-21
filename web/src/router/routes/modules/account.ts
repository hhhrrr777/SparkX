import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const APPS: AppRouteRecordRaw = {
  path: '/account',
  name: 'account',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.dashboard.users',
    requiresAuth: true,
    icon: 'icon-user',
    hideInMenu: false,
    order: 3,
  },
  children: [
    {
      path: 'index',
      name: 'AccountIndex',
      component: () => import('@/views/account/index.vue'),
      meta: {
        locale: 'menu.dashboard.users',
        requiresAuth: true,
        roles: ['*'],
        hideInMenu: true,
        activeMenu: 'account'
      },
    },
  ],
};

export default APPS;

