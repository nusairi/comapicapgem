package APITesting.com.org.api;
import APITesting.com.org.api.Classes.HotelBook;
import APITesting.com.org.api.Classes.RoomBook;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import static com.jayway.restassured.RestAssured.*;
import org.testng.Assert;
import org.testng.annotations.Test;


public class HotelBooking {

    private String _token;


    // //Hotel auth returning token
    @Test
    public void Hotel_01(){
        Response resp =
                given().
                        body("  {\"username\": \"admin\"," +
                                "\"password\": \"password\"}").
                        when().
                        contentType(ContentType.JSON).
                        post("http://localhost:3004/auth");

        System.out.println("this is response" +resp.asString());

    }



    //Login in with the auth code

    @Test
    public String Hotel_login(){
        String resp =
                given().
                        body("  {\"username\": \"admin\"," +
                                "\"password\": \"password\"}").
                        when().
                        contentType(ContentType.JSON).
                        post("http://localhost:3004/auth").
                        then()
                        .extract().response().path("token").toString();
        System.out.println("Logged in with the token ---> " +resp);
        return resp;
    }

    //Hotel Create Entry Scenario by passing the previous test in header
    @Test
    public String Hotel_Create(){

        _token = "token="+ Hotel_login();
        String requestBody = "{" +
                "\"name\" : \"Mir Hotel\"," +
                "\"address\" : \"2 High Street, Avenue place, Londonn, BS16 \"," +
                "\"regdate\" : \"2014-01-01T00:00:00.000Z\"," +
                "\"contact\" : {" +
                "\"name\" : \"Ali Mir\","+
                "\"phone\" : \"0189 9087776\","+
                "\"email\" : \"mir@email.com\" } }";
        String hotelId =  given()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Cookie", _token)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .post("http://localhost:3001/hotel")
                .then().extract().path("hotelid").toString();

        System.out.println("Hotel created with Id ---> " + hotelId);

        return hotelId;
    }


    // This scenario will create a booking for the hotel created in the previous scenario
    @Test
    public String DoBooking()
    {
        String hotelId = Hotel_Create();
        String booking = "{\"hotelid\": " +hotelId+ "," +
                "      \"firstname\": \"Mir\"," +
                "      \"lastname\": \"Ali\"," +
                "      \"totalprice\": 120," +
                "      \"depositpaid\": true," +
                "      \"bookingdates\": {" +
                "        \"checkin\": \"2017-11-23\"," +
                "        \"checkout\": \"2017-11-23\"" +
                "      }" +
                "    }";

        String bookingId = given()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Cookie", _token)
                .body(booking)
                .contentType(ContentType.JSON)
                .post("http://localhost:3000/booking")
                .then().extract().path("bookingid").toString();

        System.out.println("Booking made under Id ---> " + bookingId);
        return bookingId;
    }

//This scenario will delete the booking made in the earlier scenario with the Assertion.
    @Test
    public void DeleteBooking(){
        String bookingId = DoBooking();

        given()
                .header("Cookie", _token)
                .delete("http://localhost:3002/booking/" + bookingId);

        int statusCode = given()
                .get("http://localhost:3002/booking/" + bookingId)
                .then().extract().statusCode();
        Assert.assertEquals(statusCode, 404);
    }


    //This Scenario will create multiple entries
    @Test
    public String Hotel_Create22(){

        String hotelId = Hotel_Create();
        RoomBook Roombook1 = new RoomBook();
        Roombook1.setBookingdates("{" +
                "        \"checkin\": \"2017-11-23\"," +
                "        \"checkout\": \"2017-11-23\"" +
                "      }");
        Roombook1.getHotelid("1");
        Roombook1.getFirstname("Ammar");
        Roombook1.getLastname("Smith");
        Roombook1.getTotalprice("120");
        Roombook1.getDepositpaid("true");

        RoomBook Roombook2 = new RoomBook();
        Roombook1.setBookingdates("{" +
                "        \"checkin\": \"2017-11-23\"," +
                "        \"checkout\": \"2017-11-23\"" +
                "      }");
        Roombook2.getHotelid("1");
        Roombook2.getFirstname("Bristol");
        Roombook2.getLastname("Rice");
        Roombook2.getTotalprice("120");
        Roombook2.getDepositpaid("true");


        HotelBook HotelBook = new HotelBook();

        String resp =       given()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Cookie", _token)
                .body(Roombook1)
                .contentType(ContentType.JSON)
                .post("http://localhost:3001/hotel")
                .then().extract().path("hotelid").toString();

        System.out.println("Hotel created with Id ---> " + resp);

        return resp;
    }

}
