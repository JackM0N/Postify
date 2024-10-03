import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PostComponent } from './components/page-components/posts/post.component';

const routes: Routes = [
  { path: 'posts', component: PostComponent },  
  { path: '', redirectTo: '/posts', pathMatch: 'full' },  
  { path: '**', redirectTo: '/posts', pathMatch: 'full' } 
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
