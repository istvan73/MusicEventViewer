
public class MainActivity extends AppCompatActivity {

    private Button mNewBandBT;
]
    private Button myTestButton;
    private View.OnClickListener myCustomButtonListener;

    /*
    final EditText bandNameET;
    final EditText bandTypeET;
    final EditText bandRefLinkET;
    final EditText bandMembersET;
    final EditText bandMusicsET; */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SET the EditText fields
        final View myCustomView  = LayoutInflater.from(this).inflate(R.layout.activity_band_dialog, null);

        final EditText bandNameET = (EditText) myCustomView.findViewById(R.id.bandDialog_nameET);
        final EditText bandTypeET = (EditText) myCustomView.findViewById(R.id.bandDialog_typeET);
        final EditText bandRefLinkET = (EditText) myCustomView.findViewById(R.id.bandDialog_refLinkET);
        final EditText bandMembersET = (EditText) myCustomView.findViewById(R.id.bandDialog_membersET);
        final EditText bandMusicsET = (EditText) myCustomView.findViewById(R.id.bandDialog_musicsET);

        
        myTestButton = (Button) myCustomView.findViewById(R.id.bandDialog_createBT);
        mUploadDataBT = (Button) findViewById(R.id.upload_dataBT);

        Button.OnClickListener myCustomButtonListener = new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Log.i("RESULT", "Result="+ bandNameET.getText().toString());
                // here will be "created" the band after the Click on CreateButton
				// and also here is needed to be handled the datas
            }
        };

        mNewBandBT = (Button) findViewById(R.id.create_bandBT);
        mNewBandBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                // Set the dialog title
                builder.setTitle(R.string.bandDialog_app_name);
                builder.setView(R.layout.activity_band_dialog);
                builder.setCancelable(true);
                Dialog myDialog = builder.create();
                myDialog.show();
            }
        });

        
    }



//	IT IS NOT WORKING CURRENTLY => it is just the try...
    private Dialog createBandDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the dialog title
        builder.setTitle(R.string.bandDialog_app_name);
        builder.setView(R.layout.activity_band_dialog)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

/*
                        String bandName = bandNameET.getText().toString();
                        String bandType = bandTypeET.getText().toString();
                        String bandRefLink = bandRefLinkET.getText().toString();
                        List<String> bandMembers = Arrays.asList(bandMembersET.getText().toString().split(","));
                        List<String> bandMusics = Arrays.asList(bandMusicsET.getText().toString().split(","));

                        Log.i("TEXT_S", "EditText field values:"
                                + bandName + "; "
                                + bandType + "; "
                                + bandRefLink + "; "
                                + bandMembers + "; "
                                + bandMusics);
                                */
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }
}
