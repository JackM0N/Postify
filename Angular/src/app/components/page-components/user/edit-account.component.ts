import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { WebsiteUserService } from '../../../services/website-user.service';
import { ToastrService } from 'ngx-toastr';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-edit-account',
  templateUrl: './edit-account.component.html',
  styleUrls: ['../../../styles/edit-account.component.css']
})
export class EditAccountComponent implements OnInit {
  editAccountForm: FormGroup;
  selectedFile: File | null = null;

  constructor(
    private fb: FormBuilder,
    private websiteUserService: WebsiteUserService,
    private router: Router
  ) {
    this.editAccountForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      bio: [''],
      password: [''],
      confirmPassword: ['']
    });
  }

  ngOnInit(): void {
    this.loadAccountDetails();
  }

  loadAccountDetails(): void {
    this.websiteUserService.getAccount().subscribe(
      (data) => {
        this.editAccountForm.patchValue({
          fullName: data.fullName,
          email: data.email,
          bio: data.bio
        });
      },
      (error) => {
        console.error('Failed to load account details:', error);
      }
    );
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onSubmit(): void {
    if (this.editAccountForm.valid) {
      const { password, confirmPassword, ...rest } = this.editAccountForm.value;
      if (password && password !== confirmPassword) {
        alert('Passwords do not match');
        return;
      }

      const updatedAccount = {
        ...rest,
        ...(password ? { password } : {})
      };

      const formData = new FormData();
      Object.keys(updatedAccount).forEach(key => {
        formData.append(key, updatedAccount[key]);
      });

      if (this.selectedFile) {
        formData.append('profilePicture', this.selectedFile);
      }

      this.websiteUserService.updateAccount(formData).subscribe(
        (response) => {
          localStorage.setItem(environment.tokenKey, response.token);
          this.router.navigate(['/account']);
        },
        (error) => {
          console.error('Failed to update account:', error);
        }
      );
    }
  }
}
