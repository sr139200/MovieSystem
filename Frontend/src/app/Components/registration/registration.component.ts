import { Component, OnInit,EventEmitter, Output,Inject} from '@angular/core';
import { Router } from '@angular/router';
import { RestApiService } from '../../Service/rest-api.service';
import { user } from './user';
import { JwtHelperService } from '@auth0/angular-jwt';
import { MatDialog, MatDialogRef ,MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
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
  open:boolean=true;
  forgotPassword:boolean=false;
  constructor(private service:RestApiService,private router:Router,private jwtHelper: JwtHelperService,public dialog: MatDialog) { }
 
  ngOnInit(): void {
  }

  public registerNow(){
    let resp=this.service.doRegistration(this.user);
    resp.subscribe((data)=>this.message=data);
    this.open=false   
  }

  public goBackToRegister(){
    this.open=!this.open;
  }

  public async login() {
    try {
      this.error = false;
      if (this.forgotPassword) {
        const data: any = await this.service.getUser(this.user.username).toPromise();
        if(this.user.securityQuestion==data?.securityQuestion){
          this.user.password = data?.confirmPassword;
          this.forgotPassword=false;
        }else{
          this.dialog.open(registerAgainDialog,{data: {parent: this}}).afterClosed().subscribe((data) => {
        });
        }
      }
      const data: any = await this.service.login(this.user.username, this.user.password).toPromise();
      const decodedToken = this.jwtHelper.decodeToken(data);
      if(decodedToken['exp']!=''){
        localStorage.setItem("accessToken", data);
      localStorage.setItem("username", this.user.username);
      this.router.navigateByUrl("/dashboard");
      }
    } catch (err) {
      this.error = true;
    }
  }
  
}



@Component({
  selector: 'reset-password-dialog',
  templateUrl: 'registerAgainDialog.html',
})

export class registerAgainDialog{
  @Output() goBack = new EventEmitter();
  constructor( public dialogRef: MatDialogRef<registerAgainDialog>,@Inject(MAT_DIALOG_DATA) public data: any) {
  }
  close(): void {
    this.dialogRef.close();
  }

  public goToRegister(){
    this.data.parent.goBackToRegister();
    this.close();
  }
}