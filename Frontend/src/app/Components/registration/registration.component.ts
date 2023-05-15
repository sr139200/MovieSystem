import { Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import { RestApiService } from '../../Service/rest-api.service';
import { user } from './user';
import { MatDialog, MatDialogRef} from '@angular/material/dialog';
interface ResetPasswordDto{
  "userName":string,
  "newPassword":string,
  "securityAnswer":string
}
@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {

 user: user ={
  firstName:'',
  lastName:'',
  email:'',
  contact:'',
  username:'',
  password:'',
 confirmPassword:'',
 role:'',
 securityQuestion:''
};

  message:any;
  error:boolean=false;
  roles = ['admin', 'user'];
  
  constructor(private service:RestApiService,private router:Router,public dialog: MatDialog) { }
 
  ngOnInit(): void {
  }

  public registerNow(){
    let resp=this.service.doRegistration(this.user);
    resp.subscribe((data)=>this.message=data);
  }



public forgotPassword(){
  this.dialog.open(resetPasswordDialog).afterClosed().subscribe((data) => {
});
}


  public async login() {
      this.error = false;
      await this.service.login(this.user.username, this.user.password).subscribe((data)=>{
        localStorage.setItem("accessToken", data);
        localStorage.setItem("username", this.user.username);
        this.router.navigateByUrl("/dashboard");
      },(err)=>{
        this.error = true;
      });
  }
  
}


@Component({
  selector: 'reset-password-dialog',
  templateUrl: 'resetPasswordDialog.html',
})

export class resetPasswordDialog{

  reset:ResetPasswordDto={
    "userName":'',
    "newPassword":'',
    "securityAnswer":''
  }

  error:string=''
  success:string=''
  constructor( public dialogRef: MatDialogRef<resetPasswordDialog>,private service:RestApiService ) {
  }

  close(): void {
    this.dialogRef.close();
  }

  public submit(){
    this.error='';
    this.success='';
    this.service.resetPassword(this.reset).subscribe((data)=>{
      this.success=data
    },(err)=>{
      this.error=err.error;
    })
  }
}