import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['../../styles/login.component.css']
})
export class LoginComponent {
  protected loginData = {
    email: '',
    password: ''
  };

  constructor(
    private authService: AuthService, 
    private router: Router,
    private toastr: ToastrService,
    private jwtHelper: JwtHelperService
  ) {}

  onSubmit(): void {
    localStorage.clear();

    this.authService.login(this.loginData).subscribe({
      next: response => {
        localStorage.setItem(environment.tokenKey, response.token);
        this.authService.setLoggedIn(true);

        const decodedToken = this.jwtHelper.decodeToken(response.token);
        const username = decodedToken?.username;

        this.toastr.success(`Login was successful!\nWelcome back ${username}!`);
        this.router.navigate(['/']);
      },
      error: error => {
        this.toastr.error('Invalid email or password');
        console.error('Login error:', error);
      }
    });
  }
}
