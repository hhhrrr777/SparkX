import { DEFAULT_LAYOUT } from '../base';
import { AppRouteRecordRaw } from '../types';

const APPS: AppRouteRecordRaw = {
  path: '/knowledge',
  name: 'knowledge',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.dashboard.knowledge',
    requiresAuth: true,
    icon: 'icon-book',
    hideInMenu: false,
    order: 2,
  },
  children: [
    {
      path: 'index',
      name: 'KnowledgeIndex',
      component: () => import('@/views/knowledge/index.vue'),
      meta: {
        locale: 'menu.dashboard.knowledge',
        requiresAuth: true,
        roles: ['*'],
        hideInMenu: true,
        activeMenu: 'knowledge'
      },
    },
  ],
};

export default APPS;
