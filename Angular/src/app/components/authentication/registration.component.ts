import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['../../styles/registration.component.css']
})
export class RegistrationComponent {
  registrationData = {
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    fullName: '',
    bio: ''
  };

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService,
  ) {}

  register(): void {
    if (this.registrationData.password !== this.registrationData.confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    const registrationRequest = { 
        username: this.registrationData.username,
        email: this.registrationData.email,
        password: this.registrationData.password,
        fullName: this.registrationData.fullName,
        bio: this.registrationData.bio
    };

    this.authService.register(registrationRequest).subscribe(
      response => {
        this.toastr.success('Registration was successful')
        this.router.navigate(['/login'])
      },
      error => {
        this.toastr.error('Error during registration. Please try again.')
        console.error('Error during registration', error);
      }
    );
  }

  onSubmit() {
    if (this.registrationData.password !== this.registrationData.confirmPassword) {
      alert("Passwords do not match");
      return;
      }
      this.register();
  }
}
