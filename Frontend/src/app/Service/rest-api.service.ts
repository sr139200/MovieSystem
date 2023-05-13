import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RestApiService {

  public cart = [] as any;
  token:String="";
  constructor(private http: HttpClient) { }

  public setCart(cart: any) {
    this.cart = cart
  }

  public getCart() {
    return this.cart
  }

  public doRegistration(user: any) {
    return this.http.post("http://localhost:8090/moviebookingapp/register", user);
  }

  public login(username: any, password: any) {
    return this.http.post("http://localhost:8090/moviebookingapp/login", { username, password }, {"responseType": "text"});
  }

  public getUser(username:any){
    return this.http.get("http://localhost:8090/moviebookingapp/user/" + username)
  }

  public getAllMovies(){
   
    return this.http.get("http://localhost:8090/moviebookingapp/all",{
      "headers": {
        "authorization": `Bearer ${localStorage.getItem("accessToken")}`
      }
    })
  }

  public bookTicket(ticket:any){
   
    return this.http.post("http://localhost:8090/moviebookingapp/bookTicket",ticket,{
      "headers": {
        "authorization": `Bearer ${localStorage.getItem("accessToken")}`
      }
    })
  }

  public addMovie(movie:any){
   
    return this.http.post("http://localhost:8090/moviebookingapp/addMovie",movie,{
      "headers": {
        "authorization": `Bearer ${localStorage.getItem("accessToken")}`
      }
    })
  }
 
  public deleteMovie(movie:any){
   console.log(movie)
    return this.http.delete("http://localhost:8090/moviebookingapp/"+movie.movieTheater.movieName+"/"+movie.movieTheater.theaterName+"/delete",{
      "headers": {
        "authorization": `Bearer ${localStorage.getItem("accessToken")}`
      }, "responseType": "text"
    })
  }

  public resetPassword(password:any){
     return this.http.post("http://localhost:8090/moviebookingapp/"+localStorage.getItem("username")+"/forgot",password,{
       "headers": {
         "authorization": `Bearer ${localStorage.getItem("accessToken")}`
       }, "responseType": "text"
     })
   }

}
