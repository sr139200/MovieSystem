import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { catchError, map, Observable, of, take } from 'rxjs';
import { RestApiService } from './rest-api.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  
  constructor(private authService:RestApiService,
    private router: Router) { } 
  
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot):boolean {
    
    this.authService.getUser(localStorage.getItem("username")).subscribe((data)=>{
      return true
    },err=>{
      this.router.navigate(["/register"])
      return false
    })
    return true
} 
}
