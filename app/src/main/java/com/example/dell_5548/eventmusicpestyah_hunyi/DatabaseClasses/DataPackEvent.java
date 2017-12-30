package com.example.dell_5548.eventmusicpestyah_hunyi.DatabaseClasses;

/**
 * Created by DELL_5548 on 12/30/2017.
 */

public class DataPackEvent {

    private String mCreatorId;
    private String mDate;
    private String mLocationName;
    private String mName;
    private String mTime;
    private String mType;

    private DataPackEvent(DataPackEventBuilder builder) {
        this.mCreatorId = builder.mCreatorId;
        this.mDate = builder.mDate;
        this.mLocationName = builder.mLocationName;
        this.mName = builder.mName;
        this.mTime = builder.mTime;
        this.mType = builder.mType;
    }

    public String getCreatorId() {
        return mCreatorId;
    }

    public String getDate() {
        return mDate;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public String getName() {
        return mName;
    }

    public String getTime() {
        return mTime;
    }

    public String getType() {
        return mType;
    }

    public static class DataPackEventBuilder {
        private final String mCreatorId;
        private String mDate;
        private String mLocationName;
        private String mName;
        private String mTime;
        private String mType;

        public DataPackEventBuilder(String CreatorId) {
            this.mCreatorId = CreatorId;
        }

        public DataPackEventBuilder Date(String Date) {
            this.mDate = Date;
            return this;
        }

        public DataPackEventBuilder LocationName(String locationName) {
            this.mLocationName = locationName;
            return this;
        }

        public DataPackEventBuilder Name(String mName) {
            this.mName = mName;
            return this;
        }

        public DataPackEventBuilder Time(String Time) {
            this.mTime = Time;
            return this;
        }

        public DataPackEventBuilder Type(String Type) {
            this.mType = Type;
            return this;
        }

        public DataPackEvent build() {
            return new DataPackEvent(this);
        }
    }
}
