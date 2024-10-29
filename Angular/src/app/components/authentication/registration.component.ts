import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { passwordMatchValidator } from '../../validators/passwordMatchValidator';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['../../styles/registration.component.css']
})
export class RegistrationComponent {
  protected registrationForm: FormGroup;

  constructor(
    private authService: AuthService,
    private toastr: ToastrService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.registrationForm = this.formBuilder.group({
      username: [undefined, Validators.required],
      email: [undefined, [Validators.required, Validators.email]],
      password: [undefined, Validators.required],
      confirmPassword: [undefined, Validators.required],
      fullName: [undefined],
      bio: [undefined],
    }, { validators: passwordMatchValidator });
  }

  onSubmit(): void {
    if (this.registrationForm.valid) {
      const registrationData = this.registrationForm.value;

      if (registrationData.password !== registrationData.confirmPassword) {
        alert('Passwords do not match');
        return;
      } else {
        delete registrationData.confirmPassword;
      }

      this.authService.register(registrationData).subscribe({
        next: () => {
          this.toastr.success('Registration was successful')
          this.router.navigate(['/login'])
        },
        error: (error) => {
          this.toastr.error('Error during registration. Please try again.')
          console.error('Error during registration', error);
        }
      });
    }
  }
}
