package com.example.android.popularmovies;

import java.util.ArrayList;
import java.util.List;

public class MovieMockData {

    private static String posterUri1 = "https://image.tmdb.org/t/p/w220_and_h330_face/AtsgWhDnHTq68L0lLsUrCnM7TjG.jpg";
    private static String posterUri2 = "https://image.tmdb.org/t/p/w220_and_h330_face/klf9F58fbLFeKzcD48bgkd5rocW.jpg";
    private static String posterUri3 = "https://image.tmdb.org/t/p/w220_and_h330_face/zSOuQ1u8tjUCPhTC9Aujd2ICkdR.jpg";
    private static String posterUri4 = "https://image.tmdb.org/t/p/w220_and_h330_face/xL7Mf12aTuL4YcTGBVHQ1Ht2uCT.jpg";
    private static String posterUri5 = "https://image.tmdb.org/t/p/w220_and_h330_face/aYDRwraSc8SXgot5jabtspJTWZB.jpg";
    private static String posterUri6 = "https://image.tmdb.org/t/p/w220_and_h330_face/vW2dy7UyZqqpEP3bHageT9bei0o.jpg";
    private static String posterUri7 = "https://image.tmdb.org/t/p/w220_and_h330_face/whwN50Tx44h9Wf81PyWwOaR0UY7.jpg";
    private static String posterUri8 = "https://image.tmdb.org/t/p/w220_and_h330_face/bWtwxZW2lRsa9rr7xLgeqqBMofQ.jpg";
    private static String posterUri9 = "https://image.tmdb.org/t/p/w220_and_h330_face/xYmLdLYVWjLEURJ80kqdtdvuFUP.jpg";
    private static String posterUri10 = "https://image.tmdb.org/t/p/w220_and_h330_face/yobwvSSWfMhZnaosBjGrqhlQBOL.jpg";

    private static String backdropUri1 = "https://image.tmdb.org/t/p/w500_and_h282_face/d1hQaeKeAW3FBc3v6tIP5utleU0.jpg";
    private static String backdropUri2 = "https://image.tmdb.org/t/p/w500_and_h282_face/w2PMyoyLU22YvrGK3smVM9fW1jj.jpg";
    private static String backdropUri3 = "https://image.tmdb.org/t/p/w500_and_h282_face/xhKvSK0PKmqp7jass4IK8UA5QJW.jpg";
    private static String backdropUri4 = "https://image.tmdb.org/t/p/w500_and_h282_face/2k68Rso1XmNusg0Pfb8ZMIOJg6D.jpg";
    private static String backdropUri5 = "https://image.tmdb.org/t/p/w500_and_h282_face/urayLM8G7G21VR2RuVzPpC7NavS.jpg";
    private static String backdropUri6 = "https://i mage.tmdb.org/t/p/w500_and_h282_face/jCWeZSdlPgTDhJ8Io4shMHcuPz8.jpg";
    private static String backdropUri7 = "https://image.tmdb.org/t/p/w500_and_h282_face/jCWeZSdlPgTDhJ8Io4shMHcuPz8.jpg";
    private static String backdropUri8 = "https://image.tmdb.org/t/p/w500_and_h282_face/iNUuGUIHeeGsvOPxMRBnytpxxlz.jpg";
    private static String backdropUri9 = "https://image.tmdb.org/t/p/w500_and_h282_face/qAzYK4YPSWDc7aa4R43LcwRIAyb.jpg";
    private static String backdropUri10 = "https://image.tmdb.org/t/p/w500_and_h282_face/qzjtO7eTClKoFFtcMeVYWd1Js7C.jpg";

    public static List<Movie> getMoviesList () {
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie("1",posterUri1, backdropUri1, "Movie 1", "1.0", "Overview 1", "1/1/1991"));
        movies.add(new Movie("2",posterUri2, backdropUri2, "Movie 2", "2.0", "Overview 2", "2/1/1991"));
        movies.add(new Movie("3",posterUri3, backdropUri3, "Movie 3", "3.0", "Overview 3", "3/1/1991"));
        movies.add(new Movie("4",posterUri4, backdropUri4, "Movie 4", "4.0", "Overview 4", "4/1/1991"));
        movies.add(new Movie("5",posterUri5, backdropUri5, "Movie 5", "5.0", "Overview 5", "5/1/1991"));
        movies.add(new Movie("6",posterUri6, backdropUri6, "Movie 6", "6.0", "Overview 6", "6/1/1991"));
        movies.add(new Movie("7",posterUri7, backdropUri7, "Movie 7", "7.0", "Overview 7", "7/1/1991"));
        movies.add(new Movie("8",posterUri8, backdropUri8, "Movie 8", "8.0", "Overview 8", "8/1/1991"));
        movies.add(new Movie("9",posterUri9, backdropUri9, "Movie 9", "9.0", "Overview 9", "9/1/1991"));
        movies.add(new Movie("10",posterUri10, backdropUri10, "Movie 10", "10.0", "Overview 10", "10/1/1991"));

        return movies;
    }
}