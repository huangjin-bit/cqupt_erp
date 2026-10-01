/***
 * 前端路由表
 */
import { lazy } from 'react'

import Login from './page/login/index.js'
import Register from './page/register/index.js'
import Loading from './page/loading/index.js'
import Unauth from 'unauth'
const Teacher = lazy(() => import('./page/teacher/index.js'))
export default [
  {
    auth: ['unauthorized', 'student', 'teacher', 'admin'],
    path: '/loading',
    router: Loading,
  },
  {
    // auth: ['teacher', 'admin'],
    auth: ['unauthorized', 'student', 'teacher', 'admin'],
    path: '/teacher',
    router: Teacher
  },
  {
    auth: ['unauthorized', 'student', 'teacher', 'admin'],
    path: '/login',
    router: Login,
  },
  {
    auth: ['unauthorized', 'student', 'teacher', 'admin'],
    path: '/register',
    router: Register,
  },
  {
    auth: ['unauthorized', 'student', 'teacher', 'admin'],
    path: '/userprepare',
    router: lazy(() => import('./page/userprepare/index.js')),
  },
  {
    auth: [ 'unauthorized', 'student', 'teacher', 'admin'],
    path: '/gameoperate',
    router: lazy(() => import('./page/gameoperate/index.js')),
  },
  {
    auth: ['unauthorized', 'student', 'teacher', 'admin'],
    path: '/gameover',
    router: lazy(() => import('./page/gameover/index.js')),
  },
  {
    auth: ['admin'],
    path: '/superadmin',
    router: lazy(() => import('./page/superadmin/index'))
  }
]
