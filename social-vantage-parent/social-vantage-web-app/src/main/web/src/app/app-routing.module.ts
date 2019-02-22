import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HomeComponent} from './home/home.component';
import {AboutComponent} from './about/about.component';
import {SocialvrComponent} from './socialvr/socialvr.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'about',
    component: AboutComponent
  },
  {
    path: 'socialvr',
    component: SocialvrComponent
  },
  {
    path: '**',
    redirectTo: '/'
  }
];
export const appRoutes: any = RouterModule.forRoot(routes);
