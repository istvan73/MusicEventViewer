package com.example.dell_5548.eventmusicpestyah_hunyi.Models;

import java.util.List;

/**
 * Created by DELL_5548 on 12/29/2017.
 *
 * * <h2>Description:</h2><br>
 * <ul>
 * <li>This class is responsible collecting information about the new user nearly created.</li>
 * <li>Setting values makes this class viable laterly, when we need to push it up to the <b>FirebaseDatabase</b>
 * </ul>
 * <br>
 * <h2>Usage:</h2><br>
 * <ul>
 * <li>Create a new object of {@link com.example.dell_5548.eventmusicpestyah_hunyi.Models.UserModel}, the trick is about that this class is building with the help of <b>Builder pattern</b>.</li>
 * <li>Whenever you want to set a field of the user, you must call the inner builder class method for it.</li>
 * <li>The only important field is the Name of the user, as a {@link String}, what the user of this class must provide it when it is created.</li>
 * </ul>
 */

public class UserModel {

    private String mFirstName;
    private String mLastName;
    private String mUserEmail;
    private String mUserMobile;
    private String mUserGender;
    private String mUserRegistered;
    private List<String> mSubscribes;

    private UserModel(){

    }

    public UserModel(String firstName, String lastName,
                     String userEmail, String userMobile,
                     String userGender, String userRegistered,
                     List<String> subscribes) {
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mUserEmail = userEmail;
        this.mUserMobile = userMobile;
        this.mUserGender = userGender;
        this.mUserRegistered = userRegistered;
        this.mSubscribes = subscribes;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public void setEmail(String userEmail) {
        this.mUserEmail = userEmail;
    }

    public void setMobile(String userMobile) {
        this.mUserMobile = userMobile;
    }

    public void setGender(String userGender) {
        this.mUserGender = userGender;
    }

    public void setRegistered(String userRegistered) {
        this.mUserRegistered = userRegistered;
    }

    public void setSubscribes(List<String> subscribes) {
        this.mSubscribes = subscribes;
    }

    private UserModel(UserModelBuilder  builder) {
        this.mFirstName= builder.mFirstName;
        this.mLastName= builder.mLastName;
        this.mUserEmail= builder.mUserEmail;
        this.mUserMobile = builder.mUserMobile;
        this.mUserGender= builder.mUserGender;
        this.mUserRegistered = builder.mUserRegistered;
        this.mSubscribes = builder.mSubscribes;
    }

    public String getFirstName() {
        return mFirstName;
    }
    public String getLastName() {
        return mLastName;
    }
    public String getEmail() {
        return mUserEmail;
    }
    public String getMobile() {
        return mUserMobile;
    }
    public String getGender() {
        return mUserGender;
    }
    public String getRegistered() {
        return mUserRegistered;
    }

    public List<String> getSubscribes() {
        return mSubscribes;
    }

    public static class UserModelBuilder {
        private String mFirstName;
        private String mLastName;
        private String mUserEmail;
        private String mUserMobile;
        private String mUserGender;
        private String mUserRegistered;
        private List<String> mSubscribes;

        public UserModelBuilder (String FName,String LName) {
            this.mFirstName = FName;
            this.mLastName = LName;
        }

        public UserModelBuilder  Email(String firstName) {
            this.mUserEmail = firstName;
            return this;
        }
        public UserModelBuilder  Mobile(String firstName) {
            this.mUserMobile= firstName;
            return this;
        }
        public UserModelBuilder  Gender(String firstName) {
            this.mUserGender = firstName;
            return this;
        }
        public UserModelBuilder  Registered(String firstName) {
            this.mUserRegistered = firstName;
            return this;
        }
        public UserModelBuilder  Subscribes(List<String> Subscribes) {
            this.mSubscribes = Subscribes;
            return this;
        }

        public UserModel build() {
            return new UserModel(this);
        }
    }
}
