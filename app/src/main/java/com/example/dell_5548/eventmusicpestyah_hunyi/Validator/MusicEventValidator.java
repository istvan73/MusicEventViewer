package com.example.dell_5548.eventmusicpestyah_hunyi.Validator;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by Pista on 2017.12.28..
 *
 * <h2>Description:</h2><br>
 * <ul>
 * <li>This class is responsible for validating information gained by the user.</li>
 * <li>Each validator function requires a <b>String</b> and returns:
 * <ul>
 *     <li><u>True</u>, if the input fits the validator's requirements.</li>
 *     <li><u>False</u>, if the input does not fit the validator's requirements.</li>
 * </ul>
 * </ul>
 * <br>
 * <h2>Usage:</h2><br>
 * <ul>
 * <li>Create a new object of {@link MusicEventValidator}, add the {@link Context} as parameter (it is used to show errors via a {@link Toast})</li>
 * <li>Whenever you want to use a validator, just call it's desired class function.</li>
 * </ul>
 */

public class MusicEventValidator {

    private Context ctx;
    private int simpleStringMaxLength = 300;
    private int validDateLen = 10;
    private int validTimeLen = 5;

    //For later use
    //private int simpleStringMinLength = 2;

    public MusicEventValidator(Context ctx) {
        this.ctx = ctx;
    }


    /**
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>This method checks if a string matching our standard (0 <length < simpleStringMaxLength).</li>
     *     <li>If fails, displays the error and returns with false</li>
     * </ul>
     *
     * @param placeholderName it tells us (via {@link Toast}), which of our fields is the mistaken
     * @param simpleString the {@link String} to validate
     * @return boolean - true, if the {@link String} is valid
     */
    public boolean isValidSimpleString(String placeholderName,String simpleString){
        int strLen = simpleString.length();
        if (strLen > simpleStringMaxLength){
            Toast.makeText(ctx, "The " + placeholderName + " must be shorter than " + simpleStringMaxLength + " characters!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (simpleString == null || strLen==0){
            Toast.makeText(ctx, "Please enter data to " + placeholderName + "!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>This method checks if a string matching our standard date format (YYYY/MM/DD).</li>
     *     <li>If fails, displays the error and returns with false</li>
     *     <li>The Regexp isn't bulletproof yet, meaning some months may be mistaken <i>(20xx/02/31)</i></li>
     * </ul>
     * @param date the input to validate
     * @return boolean - true, if the {@link String} is valid
     */
    public boolean isValidDate(String date){
        int strLen = date.length();
        if (strLen != validDateLen){
            Toast.makeText(ctx, "The Date must be exactly" + validDateLen + " characters long!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!date.matches("(\\d{4})/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])")){
            Toast.makeText(ctx, "Please enter a valid date, like YEAR/MON/DAY!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>This method checks if a string matching our standard time format (HH:MM).</li>
     *     <li>If fails, displays the error and returns with false</li>
     * </ul>
     * @param time the input to validate
     * @return boolean - true, if the {@link String} is valid
     */
    public boolean isValidTime(String time){
        int strLen = time.length();
        if (strLen != validTimeLen){
            Toast.makeText(ctx, "The Time must be exactly " + validTimeLen + " characters long!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!time.matches("([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]")){
            Toast.makeText(ctx, "Please enter a valid time of a day, like Hour:Second!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
