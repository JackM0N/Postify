import { AuthInterceptor } from './services/auth.intereptor';
import { LoginComponent } from './components/authentication/login.component';
import { RegistrationComponent } from './components/authentication/registration.component';
import { NotificationsComponent } from './components/page-components/notifications/notification.component';
import { PostListComponent } from './components/page-components/posts/post-list.component';
import { PostFormDialogComponent } from './components/page-components/posts/post-form-dialog.component';
import { ToastrModule } from 'ngx-toastr';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { JwtModule, JwtHelperService } from '@auth0/angular-jwt';
import { FollowedPostsComponent } from './components/page-components/posts/followed-post.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MyPostsComponent } from './components/page-components/posts/my-post.component';
import { AccountComponent } from './components/page-components/user/account.component';
import { EditAccountComponent } from './components/page-components/user/edit-account.component';
import { environment } from '../environments/environment';
import { PopupDialogComponent } from './components/popup.component';
import { FollowedUsersComponent } from './components/page-components/followers/followed.component';
import { ProfileComponent } from './components/page-components/user/profile.component';

export function tokenGetter() {
  if (typeof window !== 'undefined' && window.localStorage) {
    return localStorage.getItem(environment.tokenKey);
  } else {
    return null;
  }
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegistrationComponent,
    FollowedPostsComponent,
    NotificationsComponent,
    PostListComponent,
    PostFormDialogComponent,
    MyPostsComponent,
    AccountComponent,
    EditAccountComponent,
    PopupDialogComponent,
    FollowedUsersComponent,
    ProfileComponent,
  ],
  exports: [
    PostListComponent,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    ToastrModule.forRoot(),
    JwtModule.forRoot({
      config: {
        tokenGetter: tokenGetter,
        allowedDomains: [environment.apiUrl],  // Defines the allowed domains for which the JWT will be sent
        disallowedRoutes: [environment.apiUrl + '/login', environment.apiUrl + '/register'],  // Defines the routes where the JWT should not be sent
      }
    })
  ],
  providers: [
    JwtHelperService,
    provideAnimationsAsync(),
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true},
    provideHttpClient(withInterceptorsFromDi())
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
