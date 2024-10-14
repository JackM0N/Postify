import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PostComponent } from './components/page-components/posts/post.component';
import { LoginComponent } from './components/authentication/login.component';
import { RegistrationComponent } from './components/authentication/registration.component';
import { FollowedPostsComponent } from './components/page-components/posts/followed-post.component';
import { NotificationsComponent } from './components/page-components/notifications/notification.component';

const routes: Routes = [
  { path: 'posts', component: PostComponent },
  { path: 'login', component: LoginComponent},
  { path: 'register', component: RegistrationComponent},  
  { path: 'followed-posts', component: FollowedPostsComponent},
  { path: 'notifications', component: NotificationsComponent},
  { path: '', redirectTo: '/posts', pathMatch: 'full' },
  { path: '**', redirectTo: '/posts', pathMatch: 'full' },
  { path: 'register', redirectTo: '/login', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
