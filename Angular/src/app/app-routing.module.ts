import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PostComponent } from './components/page-components/posts/post.component';
import { LoginComponent } from './components/page-components/authentication/login.component';

const routes: Routes = [
  { path: 'posts', component: PostComponent },
  { path: 'login', component: LoginComponent},  
  { path: '', redirectTo: '/posts', pathMatch: 'full' },
  { path: '**', redirectTo: '/posts', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
