import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { WebsiteUserService } from '../../../services/website-user.service';

@Component({
  selector: 'app-edit-account',
  templateUrl: './edit-account.component.html',
  styleUrls: ['../../../styles/edit-account.component.css']
})
export class EditAccountComponent implements OnInit {
  editAccountForm: FormGroup;

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

      console.log('Submitting updated account:', updatedAccount); // Debugging line

      this.websiteUserService.updateAccount(updatedAccount).subscribe(
        (response) => {
          console.log('Update response:', response); // Debugging line
          this.router.navigate(['/account']);
        },
        (error) => {
          console.error('Failed to update account:', error);
        }
      );
    }
  }
}
