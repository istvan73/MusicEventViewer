package com.example.dell_5548.eventmusicpestyah_hunyi.Models;

import java.io.StringReader;

/**
 * Created by DELL_5548 on 12/30/2017.
 */

public class EventModel {

    private String creatorId;
    private String date;
    private String locationName;
    private String name;
    private String time;
    private String type;
    private String imagePath;
    private String description;
    private String coordinates;
    private String key;

    private EventModel(){

    }

    public EventModel(String creatorId,
                      String date,
                      String locationName,
                      String name,
                      String time,
                      String type,
                      String imagePath,
                      String description,
                      String coordinates) {
        this.creatorId = creatorId;
        this.date = date;
        this.locationName = locationName;
        this.name = name;
        this.time = time;
        this.type = type;
        this.imagePath = imagePath;
        this.description = description;
        this.coordinates = coordinates;
    }

    private EventModel(EventModelBuilder builder) {
        this.creatorId = builder.mCreatorId;
        this.date = builder.mDate;
        this.locationName = builder.mLocationName;
        this.name = builder.mName;
        this.time = builder.mTime;
        this.type = builder.mType;
        this.imagePath = builder.mImagePath;
        this.description = builder.mDescription;
        this.coordinates = builder.mCoordinates;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getDate() {
        return date;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }


    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() { return imagePath; }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }



    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public static class EventModelBuilder {
        private final String mCreatorId;
        private String mDate;
        private String mLocationName;
        private String mName;
        private String mTime;
        private String mType;
        private String mImagePath;
        private String mDescription;
        private String mCoordinates;

        public EventModelBuilder(String CreatorId) {
            this.mCreatorId = CreatorId;
        }

        public EventModelBuilder Date(String Date) {
            this.mDate = Date;
            return this;
        }

        public EventModelBuilder LocationName(String locationName) {
            this.mLocationName = locationName;
            return this;
        }

        public EventModelBuilder Name(String mName) {
            this.mName = mName;
            return this;
        }

        public EventModelBuilder Time(String Time) {
            this.mTime = Time;
            return this;
        }

        public EventModelBuilder Type(String Type) {
            this.mType = Type;
            return this;
        }

        public EventModelBuilder ImagePath(String mImagePath) {

            this.mImagePath = mImagePath.length()!=0?mImagePath:null;
            return this;
        }

        public EventModelBuilder Description(String mDescription) {
            this.mDescription = mDescription;
            return this;
        }

        public EventModelBuilder Coordinates(String mCoordinates) {
            this.mCoordinates = mCoordinates;
            return this;
        }



        public EventModel build() {
            return new EventModel(this);
        }
    }
}
