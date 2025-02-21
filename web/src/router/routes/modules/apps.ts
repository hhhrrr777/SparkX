import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const APPS: AppRouteRecordRaw = {
  path: '/apps',
  name: 'apps',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.dashboard.apps',
    requiresAuth: true,
    icon: 'icon-robot',
    hideInMenu: false,
    order: 1,
  },
  children: [
    {
      path: 'index',
      name: 'AppsIndex',
      component: () => import('@/views/apps/index.vue'),
      meta: {
        locale: 'menu.dashboard.apps',
        requiresAuth: true,
        roles: ['*'],
        hideInMenu: true,
        activeMenu: 'apps'
      },
    },
  ],
};

export default APPS;
