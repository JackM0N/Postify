import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PostComponent } from './components/page-components/posts/post.component';
import { LoginComponent } from './components/authentication/login.component';
import { RegistrationComponent } from './components/authentication/registration.component';
import { FollowedPostsComponent } from './components/page-components/posts/followed-post.component';
import { NotificationsComponent } from './components/page-components/notifications/notification.component';
import { MyPostsComponent } from './components/page-components/posts/my-post.component';
import { AccountComponent } from './components/page-components/user/account.component';
import { EditAccountComponent } from './components/page-components/user/edit-account.component';
import { ProfileComponent } from './components/page-components/user/profile.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent},
  { path: 'register', component: RegistrationComponent},
  { path: 'account', component: AccountComponent},
  { path: 'account/edit', component: EditAccountComponent},
  { path: 'profile/:username', component: ProfileComponent },

  { path: 'posts', component: PostComponent },
  { path: 'followed-posts', component: FollowedPostsComponent},
  { path: 'my-posts', component: MyPostsComponent},

  { path: 'notifications', component: NotificationsComponent},
  
  { path: '', redirectTo: '/posts', pathMatch: 'full' },
  { path: '**', redirectTo: '/posts', pathMatch: 'full' },
  { path: 'register', redirectTo: '/login', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}