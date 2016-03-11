package com.gmail.lgelberger.popularmovies;

/**
 * Created by Leslie on 2016-03-03.
 * <p>
 * Modelled after https://plus.google.com/s/ListView%20with%20Custom%20Adapter%20Prabeesh%20R%20K/top
 * <p>
 * Will provide each grid item of data as an object
 * Just contains the moviePoster id (as int)
 * and the text for the movie Title
 * <p>
 * as you get the information from a JSON file or url call, instantiate this class for each item
 * This can be "added" to the movieAdapter. I.e. these are the objects in the movieApater internal ArrayList
 * <p>
 * <p>
 * 2016-03-07 :JG added movie_poster URL
 * Will have to delete movie_poster resource eventually
 */
public class MovieDataProvider {
    private int moviePosterResource; //for test display should be able to delete this and the getter setter methods
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
     *  empty constructor
     */
    public MovieDataProvider() {

    }

    /**
     * constructor
     *
     * @param moviePosterResource
     * @param movieTitle
     */
    public MovieDataProvider(int moviePosterResource, String movieTitle) {
        this.moviePosterResource = moviePosterResource;
        this.movieTitle = movieTitle;
    }

    /**
     * constructor
     *
     * @param moviePosterResource ID of movie poster
     * @param movieTitle
     * @param moviePosterUrl      url of movie
     */
    public MovieDataProvider(int moviePosterResource, String movieTitle, String moviePosterUrl) {
        this.setMoviePosterResource(moviePosterResource);
        this.setMovieTitle(movieTitle);
        this.setMoviePosterUrl(moviePosterUrl);
    }

    /**
     * constructor - the ONE I"M USING RIGHT NOW
     *
     * @param movieTitle
     * @param moviePosterUrl of poster
     */
    public MovieDataProvider(String movieTitle, String moviePosterUrl) {
        this.setMovieTitle(movieTitle);
        this.setMoviePosterUrl(moviePosterUrl);
    }

    public String getMoviePosterUrl() {
        return moviePosterUrl;
    }

    public void setMoviePosterUrl(String moviePosterUrl) {
        this.moviePosterUrl = moviePosterUrl;
    }

    public int getMoviePosterResource() {
        return moviePosterResource;
    }

    public void setMoviePosterResource(int moviePosterResource) {
        this.moviePosterResource = moviePosterResource;
    }

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


}
