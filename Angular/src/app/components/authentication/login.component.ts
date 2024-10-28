import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from '../../../environments/environment';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['../../styles/login.component.css']
})
export class LoginComponent {
  protected loginForm: FormGroup;

  constructor(
    private authService: AuthService, 
    private router: Router,
    private toastr: ToastrService,
    private jwtHelper: JwtHelperService,
    private formBuilder: FormBuilder,
  ) {
    this.loginForm = this.formBuilder.group({
      email: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      const loginData = this.loginForm.value;
      localStorage.clear();

      this.authService.login(loginData).subscribe({
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
}
