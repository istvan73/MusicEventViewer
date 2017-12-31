import java.util.List;

/**
 * Created by DELL_5548 on 12/29/2017.
 *
 * * <h2>Description:</h2><br>
 * <ul>
 * <li>This class is responsible collecting information about the new band nearly created.</li>
 * <li>Setting values makes this class viable laterly, when we need to push it up to the <b>FirebaseDatabase</b>
 * </ul>
 * <br>
 * <h2>Usage:</h2><br>
 * <ul>
 * <li>Create a new object of {@link com.example.dell_5548.eventmusicpestyah_hunyi.DatabaseClasses.DataPackBand}, the trick is about that this class is building with the help of <b>Builder pattern</b>.</li>
 * <li>Whenever you want to set a field of the band, you must call the inner builder class method for it.</li>
 * <li>The only important field is the Name of the band, as a {@link String}, what the user of this class must provide it when it is created.</li>
 * </ul>
 */

public class DataPackBand {

    private String mName;
    private String mType;
    private String mRefLink;
    private List<String> mMembers;
    private List<String> mMusicTitles;

    private DataPackBand(DataPackEventBuilder builder) {
        this.mName = builder.mName;
        this.mType = builder.mType;
        this.mRefLink = builder.mRefLink;
        this.mMembers = builder.mMembers;
        this.mMusicTitles = builder.mMusicTitles;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }

    public String getRefLink() {
        return mRefLink;
    }

    public List<String> getMembers() {
        return mMembers;
    }

    public List<String> getMusicTitles() {
        return mMusicTitles;
    }

    public static class DataPackEventBuilder {
        private final String mName;
        private String mType;
        private String mRefLink;
        private List<String> mMembers;
        private List<String> mMusicTitles;

        public DataPackEventBuilder(String Name) {
            this.mName = Name;
        }

        public DataPackEventBuilder Type(String Type) {
            this.mType = Type;
            return this;
        }

        public DataPackEventBuilder RefLink(String RefLink) {
            this.mRefLink = RefLink;
            return this;
        }

        public DataPackEventBuilder Members(List<String> Members) {
            this.mMembers = Members;
            return this;
        }

        public DataPackEventBuilder MusicTitles(List<String> MusicTitles) {
            this.mMusicTitles = MusicTitles;
            return this;
        }

        public DataPackBand build() {
            return new DataPackBand(this);
        }
    }
}
