import { Component, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { RestApiService } from 'src/app/Service/rest-api.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

interface MovieInfo {
  movieTheater: {
    movieName: string;
    theaterName: string;
  };
  availableTickets: number;
  availableSeatsNumbers: number[];
  ticketStatus: string;
}

interface Seat {
  number: number;
  available: boolean;
  selected: boolean;
}

interface MovieDto{
  movieName: string;
    theaterName: string;
    availableTickets:number;
}


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
  search : any;
  allMovies:any;
  ticketDialog:boolean=false;
  movieInfo!: MovieInfo;
  movieDto!:MovieDto;
  seats: Seat[] = [];
  isAdmin:boolean=false;
  expiration!: Date;
  token:any;
  newPassword:any;
  
  

  constructor(private service:RestApiService,public dialog: MatDialog,private router:Router) { }

  ngOnInit(): void {
    this.getUser(localStorage.getItem("username"));
    this.getAllMovies();
  }

  public getUser(token :any){
    this.service.getUser(token).subscribe((data:any)=>{
      if(data){
        if(data["role"]=="admin"){
          this.isAdmin=true;
        }
      }
    })
  }

  public logout() {
    localStorage.removeItem("accessToken")
    localStorage.removeItem("username")
    this.router.navigateByUrl("/register")
  }
  
  public deleteMovie(movie:any){
    this.service.deleteMovie(movie).subscribe((data)=>{
      this.getAllMovies();
    })
  }

  public getAllMovies(){
    this.service.getAllMovies().subscribe((data)=>this.allMovies=data);
  }

  public bookTicket(movie:any){
    this.seats=[];
    this.movieInfo = movie;
   for(var i=0;i<100;i++ ){
      this.seats.push({
        number:i+1,available:this.movieInfo.availableSeatsNumbers.includes(i+1),selected:!this.movieInfo.availableSeatsNumbers.includes(i+1)
      });
    }
   
    this.dialog.open(bookTicketDialog, {
      data:{
        movieInfo:this.movieInfo,
        seats:this.seats
      }
    }).afterClosed().subscribe((data) => {
      if (data) {
        let selectedSeats=data.seats.filter((s: { available: any; selected: any; })=>s.available && s.selected).map((s: { number: any; })=>s.number)
        var ticket = {
          "movieName":data.movieInfo.movieTheater.movieName,
          "theaterName":data.movieInfo.movieTheater.theaterName,
          "numberOfTickets":selectedSeats.length,
          "seatNumber":selectedSeats
      }
      this.service.bookTicket(ticket).subscribe((data)=>{
        this.getAllMovies();
      });
      }
    });
  }
  
  public addMovie(){
    this.dialog.open(addMovieDialog, {
      data:{
      movieDto:this.movieDto
      }
    }).afterClosed().subscribe((data) => {
      if(data){
      var movie={
        "movieName":data.movieName,
        "theaterName":data.theaterName,
        "availableTickets":data.availableTickets
      }
    this.service.addMovie(movie).subscribe((data)=>{
      this.getAllMovies();
    },error => {
      console.log(error)
    })
  }
  });
  }
 
}

@Component({
  selector: 'book-ticket-dialog',
  templateUrl: 'bookTicketDialog.html',
  styleUrls: ['./dashboard.component.scss']
})

export class bookTicketDialog{

  userData: any;
  constructor( public dialogRef: MatDialogRef<bookTicketDialog>,@Inject(MAT_DIALOG_DATA) public data: any) {
   this.userData = { ...data }
  }

  close(): void {
    this.dialogRef.close(this.userData);
  }

  toggleSelection(seat: Seat): void {
    if (seat.available) {
      seat.selected = !seat.selected;
    }
  }
}


@Component({
  selector: 'add-movie-dialog',
  templateUrl: 'addMovieDialog.html',
  styleUrls: ['./dashboard.component.scss']
})
export class addMovieDialog{
  movieData: any;
  constructor( public dialogRef: MatDialogRef<addMovieDialog>,@Inject(MAT_DIALOG_DATA) public data: any) {
   this.movieData = { ...data }
  }
  close(): void {
    this.dialogRef.close(this.movieData);
  }
}

