import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { JwtHelperService } from '@auth0/angular-jwt';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['../../styles/login.component.css']
})
export class LoginComponent {
  loginData = {
    email: '',
    password: ''
  };
  errorMessage: string | null = null;

  constructor(
    private authService: AuthService, 
    private router: Router,
    private toastr: ToastrService,
    private jwtHelper: JwtHelperService,) {}

  onSubmit(): void {
    this.authService.login(this.loginData).subscribe(
      response => {
        localStorage.setItem('token', response.token);

        const decodedToken = this.jwtHelper.decodeToken(response.token);
        const username = decodedToken?.username;

        this.toastr.success(`Login was successful!\nWelcome back ${username}!`);
        this.router.navigate(['/']);
      },
      error => {
        this.toastr.error('Invalid email or password');
        console.error('Login error:', error);
      }
    );
  }
}