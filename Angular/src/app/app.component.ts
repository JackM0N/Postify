import { Component, OnInit } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Router } from '@angular/router';
import { AuthService } from './services/auth.service';
import { environment } from '../environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { PopupDialogComponent } from './components/popup.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  title = 'Angular';
  isLoggedIn = false;
  username: string | null = null;

  constructor(
    private jwtHelper: JwtHelperService, 
    private router: Router,
    private authService: AuthService,
    protected dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe(isLoggedIn => {
      this.isLoggedIn = isLoggedIn;
      if (isLoggedIn) {
        this.loadUserData();
      } else {
        this.username = null;
      }
    });
  }

  loadUserData(): void {
    const token = localStorage.getItem(environment.tokenKey);
    if (token && !this.jwtHelper.isTokenExpired(token)) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      this.username = decodedToken?.username || null;
    }
  }

  openLogoutConfirmation(): void {
    const dialogRef = this.dialog.open(PopupDialogComponent, {
      data: {
        dialogTitle: 'Potwierdzenie wylogowania',
        dialogContent: 'Czy na pewno chcesz się wylogować?'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.logout();
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
