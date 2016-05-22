package com.gmail.lgelberger.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Leslie on 2016-03-03.
 *
 * This is the old Movie Data Provider
 * This class held the full data for a single movie
 * It is Parecelable so it can be sent through an Intent
 *
 * <p>
 * Modelled after https://plus.google.com/s/ListView%20with%20Custom%20Adapter%20Prabeesh%20R%20K/top
 * <p>
 * Will provide each grid item of data as an object
 * As well as all the detailed movie data for the detail view
 * <p>
 * as you get the information from a JSON file or url call, instantiate this class for each item
 * This can be "added" to the movieAdapter. I.e. these are the objects in the movieApater internal ArrayList
 * <p>
 * <p>
 *
 * Implementing Parcelable so I can pass this object in an intent
 */
public class ZZZOldMovieDataProviderMakesParcelableSingleMovieInfo implements Parcelable {
   // private int moviePosterResource; //for test display should be able to delete this and the getter setter methods
    //really just used to place a default picture in if needed. But I can use Picasso placeholder picture options for this

    private String movieTitle;
    private String moviePosterUrl;

    private String originalTitle;
    private String overview;  //A plot synopsis (called overview in the api)
    private String voteAverage; //user rating (called vote_average in the api)
    private String releaseDate;

    /*
FROM project description of detail screen

    Allow the user to tap on a movie poster and transition to a details screen with additional information such as:
original title
movie poster image thumbnail
A plot synopsis (called overview in the api)
user rating (called voteAverage in the api)
release date
    */


    /**
     * empty constructor
     */
    public ZZZOldMovieDataProviderMakesParcelableSingleMovieInfo() {

    }

    /**
     * constructor
     *
     * @param moviePosterResource
     * @param movieTitle
     */
   /* public ZZZOLDMovieDataProvider(int moviePosterResource, String movieTitle) {//commented out as I'm not using moviePosterResource
        this.moviePosterResource = moviePosterResource;
        this.movieTitle = movieTitle;
    }*/

    /**
     * constructor
     *
     * @param moviePosterResource ID of movie poster
     * @param movieTitle
     * @param moviePosterUrl      url of movie
     */
  /*  public ZZZOLDMovieDataProvider(int moviePosterResource, String movieTitle, String moviePosterUrl) {/commented out as I'm not using moviePosterResource
        this.setMoviePosterResource(moviePosterResource);
        this.setMovieTitle(movieTitle);
        this.setMoviePosterUrl(moviePosterUrl);
    }*/

    /**
     * constructor - the ONE I"M USING RIGHT NOW
     *
     * @param movieTitle
     * @param moviePosterUrl of poster
     */
    public ZZZOldMovieDataProviderMakesParcelableSingleMovieInfo(String movieTitle, String moviePosterUrl) {
        this.setMovieTitle(movieTitle);
        this.setMoviePosterUrl(moviePosterUrl);
    }

    public String getMoviePosterUrl() {
        return moviePosterUrl;
    }
    public void setMoviePosterUrl(String moviePosterUrl) {
        this.moviePosterUrl = moviePosterUrl;
    }

  /*  public int getMoviePosterResource() {/commented out as I'm not using moviePosterResource
        return moviePosterResource;
    }
    public void setMoviePosterResource(int moviePosterResource) {/commented out as I'm not using moviePosterResource
        this.moviePosterResource = moviePosterResource;
    }*/

    public String getMovieTitle() {
        return movieTitle;
    }
    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getOverview() {
        return overview;
    }
    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }
    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }




//////////////////Parcelable Stuff From http://shri.blog.kraya.co.uk/2010/04/26/android-parcel-data-to-pass-between-activities-using-parcelable-classes/

    /**
     * Constructor to use when re-constructing object
     * from a parcel
     *
     * @param in a parcel from which to read this object
     */
    public ZZZOldMovieDataProviderMakesParcelableSingleMovieInfo(Parcel in) {
        readFromParcel(in);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // We just need to write each field into the
        // parcel. When we read from parcel, they
        // will come back in the same order FIFO
      //  dest.writeInt(moviePosterResource);/commented out as I'm not using moviePosterResource
        dest.writeString(movieTitle);
        dest.writeString(moviePosterUrl);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(voteAverage);
        dest.writeString(releaseDate);
    }

    /**
     * Called from the constructor to create this
     * object from a parcel.
     *
     * @param in parcel from which to re-create object
     */
    private void readFromParcel(Parcel in) {

        // We just need to read back each
        // field in the order that it was
        // written to the parcel
      //  moviePosterResource = in.readInt();/commented out as I'm not using moviePosterResource
        movieTitle = in.readString();
        moviePosterUrl = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();

    }


    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays.
     * <p>
     * This also means that you can use use the default
     * constructor to create the object and use another
     * method to hyrdate it as necessary.
     * <p>
     * I just find it easier to use the constructor.
     * It makes sense for the way my brain thinks ;-)
     */
    public static final Creator CREATOR =
            new Creator() {
                public ZZZOldMovieDataProviderMakesParcelableSingleMovieInfo createFromParcel(Parcel in) {
                    return new ZZZOldMovieDataProviderMakesParcelableSingleMovieInfo(in);
                }

                public ZZZOldMovieDataProviderMakesParcelableSingleMovieInfo[] newArray(int size) {
                    return new ZZZOldMovieDataProviderMakesParcelableSingleMovieInfo[size];
                }
            };
}
