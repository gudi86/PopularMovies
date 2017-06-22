package br.com.gustavo.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by gustavomagalhaes on 6/16/17.
 */

public class Movie implements Parcelable {
    private Integer id;
    private String title;
    private String overview;
    private Double rated;
    private Date releaseDate;
    private String posterPath;

    public Movie(Integer id, String title, String overview, Double rated, Date releaseDate, String posterPath) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.rated = rated;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        rated = in.readDouble();
        releaseDate = new Date(in.readLong());
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public Double getRated() {
        return rated;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    @Override
    public String toString() {
        return "{id:" + id + ", " +
                "title:" + title+ ", " +
                "overview:" + overview + ", " +
                "rated:" + rated + ", " +
                "release_date:" + releaseDate.toString() + ", " +
                "poster_path:" + posterPath + "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(posterPath);
        parcel.writeDouble(rated);
        parcel.writeLong(releaseDate.getTime());
    }
}
