package neel.com.jatra.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PopularPlace implements Parcelable {

    String title;
    String introduction;
    String recommendation;
    String guideline;
    String budget;
    String estimated_time;
    String description;
    String photo_url;
    String video_url;


    public PopularPlace() {
    }

    public PopularPlace(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public PopularPlace(String title, String description, String photo_utl) {
        this.title = title;
        this.description = description;
        this.photo_url = photo_utl;
    }

    public PopularPlace(String title, String description, String photo_url, String video_url) {
        this.title = title;
        this.description = description;
        this.photo_url = photo_url;
        this.video_url = video_url;
    }


    public PopularPlace(String title, String introduction, String recommendation, String guideline, String budget, String estimated_time, String description, String photo_url, String video_url) {
        this.title = title;
        this.introduction = introduction;
        this.recommendation = recommendation;
        this.guideline = guideline;
        this.budget = budget;
        this.estimated_time = estimated_time;
        this.description = description;
        this.photo_url = photo_url;
        this.video_url = video_url;
    }


    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getGuideline() {
        return guideline;
    }

    public void setGuideline(String guideline) {
        this.guideline = guideline;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getEstimated_time() {
        return estimated_time;
    }

    public void setEstimated_time(String estimated_time) {
        this.estimated_time = estimated_time;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto_utl() {
        return photo_url;
    }

    public void setPhoto_utl(String photo_utl) {
        this.photo_url = photo_utl;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(title);
        parcel.writeString(introduction);
        parcel.writeString(recommendation);
        parcel.writeString(guideline);
        parcel.writeString(budget);
        parcel.writeString(estimated_time);
        parcel.writeString(description);
        parcel.writeString(photo_url);
        parcel.writeString(video_url);

    }


    private PopularPlace(Parcel in){

        title = in.readString();
        introduction = in.readString();
        recommendation = in.readString();
        guideline = in.readString();
        budget = in.readString();
        estimated_time = in.readString();
        description = in.readString();
        photo_url = in.readString();
        video_url = in.readString();

    }

    public static final Creator<PopularPlace> CREATOR = new Creator<PopularPlace>() {
        @Override
        public PopularPlace createFromParcel(Parcel parcel) {
            return new PopularPlace(parcel);
        }

        @Override
        public PopularPlace[] newArray(int i) {
            return new PopularPlace[i];
        }
    };






}
