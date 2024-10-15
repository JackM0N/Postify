import { AuthInterceptor } from './services/auth.intereptor';
import { LoginComponent } from './components/authentication/login.component';
import { RegistrationComponent } from './components/authentication/registration.component';

import { NotificationsComponent } from './components/page-components/notifications/notification.component';
import { PostListComponent } from './components/page-components/posts/post-list.component';

import { ToastrModule } from 'ngx-toastr';
import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { HttpClientModule } from '@angular/common/http';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { JwtModule, JwtHelperService } from '@auth0/angular-jwt';
import { FollowedPostsComponent } from './components/page-components/posts/followed-post.component';

export function tokenGetter() {
  if (typeof window !== 'undefined' && window.localStorage) {
    return localStorage.getItem('token');
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
  ],
  exports: [
    PostListComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ToastrModule.forRoot(),
    JwtModule.forRoot({
      config: {
        tokenGetter: tokenGetter,  // Defines how to retrieve the token
        allowedDomains: ['localhost:8080'],  // Defines the allowed domains for which the JWT will be sent
        disallowedRoutes: ['localhost:8080/login', 'localhost:8080/register'],  // Defines the routes where the JWT should not be sent
      }
    })
  ],
  providers: [
    JwtHelperService,
    provideClientHydration(),
    provideAnimationsAsync(),
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
