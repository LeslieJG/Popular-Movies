package com.gmail.lgelberger.popularmovies;

/**
 * Created by Leslie on 2016-03-03.
 *
 * Modelled after https://plus.google.com/s/ListView%20with%20Custom%20Adapter%20Prabeesh%20R%20K/top
 *
 * Will provide each grid item of data as an object
 * Just contains the moviePoster id (as int)
 * and the text for the movie Title
 *
 * as you get the information from a JSON file or url call, instantiate this class for each item
 * This can be "added" to the movieAdapter. I.e. these are the objects in the movieApater internal ArrayList
 */
public class MovieDataProvider {
    private int movie_poster_resource;
    private String movie_title;

    /**
     *
     * @param movie_poster_resource ID of movie poster
     * @param movie_title
     */
    public MovieDataProvider(int movie_poster_resource, String movie_title){
        this.setMovie_poster_resource(movie_poster_resource);
        this.setMovie_title(movie_title);
    }

    public int getMovie_poster_resource() {
        return movie_poster_resource;
    }

    public void setMovie_poster_resource(int movie_poster_resource) {
        this.movie_poster_resource = movie_poster_resource;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }
}
