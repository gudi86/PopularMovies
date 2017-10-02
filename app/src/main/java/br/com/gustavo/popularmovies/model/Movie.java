package br.com.gustavo.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by gustavomagalhaes on 6/16/17.
 */

public class Movie implements Parcelable {

    private Integer id;
    private String title;
    private String overview;
    @SerializedName("vote_average")
    private Double rated;
    @SerializedName("release_date")
    private Date releaseDate;
    @SerializedName("poster_path")
    private String posterPath;
    private byte[] image;

    public Movie(Integer id, String title, String overview, Double rated, Date releaseDate, String posterPath) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.rated = rated;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
    }

    public Movie(Integer id, String title, String overview, Double rated, Date releaseDate, String posterPath, byte[] image) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.rated = rated;
        this.releaseDate = releaseDate;
        this.image = image;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        rated = in.readDouble();
        releaseDate = new Date(in.readLong());
        if (image != null) {
            in.readByteArray(image);
        }
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

    public String getReleaseDateFormat() {
        return new SimpleDateFormat("yyyy", Locale.getDefault()).format(releaseDate);
    }

    public String getPosterPath() {
        return posterPath;
    }

    public byte[] getImage() {
        return image;
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
        if (image != null) {
            parcel.writeByteArray(image);
        }
    }
}
