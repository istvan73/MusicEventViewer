package com.example.dell_5548.eventmusicpestyah_hunyi.DatabaseClasses;

/**
 * Created by DELL_5548 on 12/30/2017.
 *
 * /**
 * * <h2>Description:</h2><br>
 * <ul>
 * <li>This class is responsible collecting information about the new event nearly created.</li>
 * <li>Setting values makes this class viable laterly, when we need to push it up to the <b>FirebaseDatabase</b>
 * </ul>
 * <br>
 * <h2>Usage:</h2><br>
 * <ul>
 * <li>Create a new object of {@link com.example.dell_5548.eventmusicpestyah_hunyi.DatabaseClasses.DataPackEvent}, the trick is about that this class is building with the help of <b>Builder pattern</b>.</li>
 * <li>Whenever you want to set a field of the event, you must call the inner builder class method for it.</li>
 * <li>The only important field is the Creator ID as a {@link String}, what the user of this class must provide it when it is created.</li>
 * </ul>
 */

public class DataPackEvent {

    private String mCreatorId;
    private String mDate;
    private String mLocationName;
    private String mName;
    private String mTime;
    private String mType;
    private String mImagePath;

    private DataPackEvent(DataPackEventBuilder builder) {
        this.mCreatorId = builder.mCreatorId;
        this.mDate = builder.mDate;
        this.mLocationName = builder.mLocationName;
        this.mName = builder.mName;
        this.mTime = builder.mTime;
        this.mType = builder.mType;
        this.mImagePath = builder.mImagePath;
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

    public String getImagePath() { return mImagePath; }


    public static class DataPackEventBuilder {
        private final String mCreatorId;
        private String mDate;
        private String mLocationName;
        private String mName;
        private String mTime;
        private String mType;
        private String mImagePath;

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

        public DataPackEventBuilder ImagePath(String mImagePath) {

            this.mImagePath = mImagePath.length()==0?mImagePath:null;
            return this;
        }

        public DataPackEvent build() {
            return new DataPackEvent(this);
        }
    }
}
