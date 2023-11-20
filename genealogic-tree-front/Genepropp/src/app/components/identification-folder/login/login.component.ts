import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { IdentificationService } from '../../../services/identificaton/identification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email: string = '';
  password: string = '';

  constructor(
    private identificationService: IdentificationService,
    private router: Router,
    private cookieService: CookieService
    ) {}

  onSubmit() {
    this.identificationService.loginattempt(this.email, this.password)
      .subscribe((response) => {
        if (response.success) {
          this.cookieService.set('userId', response.value.id);
          this.router.navigate(['homePage']);
        }
        else {
          console.error(response.message);
        }
      });
  }
}