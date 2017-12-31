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
 * <li>Create a new object of {@link com.example.dell_5548.eventmusicpestyah_hunyi.DatabaseClasses.DataPackUser}, the trick is about that this class is building with the help of <b>Builder pattern</b>.</li>
 * <li>Whenever you want to set a field of the user, you must call the inner builder class method for it.</li>
 * <li>The only important field is the Name of the user, as a {@link String}, what the user of this class must provide it when it is created.</li>
 * </ul>
 */

public class DataPackUser {

    private String mName;
    private List<String> mSubscribes;

    private DataPackUser(DataPackEventBuilder builder) {
        this.mName = builder.mName;
        this.mSubscribes = builder.mSubscribes;
    }

    public String getName() {
        return mName;
    }

    public List<String> getSubscribes() {
        return mSubscribes;
    }

    public static class DataPackEventBuilder {
        private final String mName;
        private List<String> mSubscribes;

        public DataPackEventBuilder(String Name) {
            this.mName = Name;
        }

        public DataPackEventBuilder Subscribes(List<String> Subscribes) {
            this.mSubscribes = Subscribes;
            return this;
        }

        public DataPackUser build() {
            return new DataPackUser(this);
        }
    }
}
