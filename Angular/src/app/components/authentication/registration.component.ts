import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';

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

  constructor(private authService: AuthService) {}

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
        console.log('Registration successful', response);
      },
      error => {
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