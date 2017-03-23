package com.example.shannon.albumcover;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * This app retrieves a list of albums by artists from a JSON server and allows you view the artists album covers
 * Shannon Ness 23/03/2017
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Generate the album list first
        generateAlbumList();
    }

    /**
     * Generates the list of album numes to display as buttons to click
     */
    void generateAlbumList()
    {
        try
        {
            //Retrieve a lsit of albums from the server
            String jsonString = new RetrieveAlbumActivity().execute("http://jsonplaceholder.typicode.com/albums").get();
            //Turn the information into json array
            JSONArray albumArray = new JSONArray(jsonString);
            //Retrieve the linear layout from the xml design
            LinearLayout layout = (LinearLayout) findViewById(R.id.layout1);
            //Clear anythign inside of it
            layout.removeAllViews();
            //Loop through the array and display the information
            for (int i = 0; i < albumArray.length(); i++)
            {
                //Reteive album information as a JSON object
                JSONObject albumJSON = albumArray.getJSONObject(i);
                //Convert into Album
                Album album = new Album(albumJSON.getInt("userId"),albumJSON.getInt("id") ,albumJSON.getString("title"));
                //Create the button for the album name
                Button butt = new Button(this);
                //Set the ID so we know which icon to retrieve
                butt.setId(album.getId());
                //Create the listerner for the album
                butt.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v) {
                        generateAlbumCoverList(((Button)v).getId());
                    }
                });
                //Add text to the button
                butt.setText(album.getTitle() + " by " + album.getUserID());
                //Add the button to the UI
                layout.addView(butt);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Generates a grid of Album covers in thumbnails that are clickable.
     * @param albumID Id of the album
     */
    void generateAlbumCoverList(final int albumID)
    {
        try
        {
            //Retrieve the layout
            LinearLayout layout = (LinearLayout) findViewById(R.id.layout1);
            //Remove the contents
            layout.removeAllViews();
            //Retrieve the photo infromation
            String jsonString = new RetrieveAlbumActivity().execute("http://jsonplaceholder.typicode.com/photos").get();
            //Array of the photo information
            JSONArray photoArray = new JSONArray(jsonString);
            //Create a back button to return to previous screen
            Button butt = new Button(this);
            //Make it say go back!
            butt.setText("Go Back");
            //Set Width
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            butt.setWidth(width);
            //Action to perform when pressed
            butt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Go back to the album list
                    generateAlbumList();
                }
            });
            //Add the button to the UI
            layout.addView(butt);
            //Add a grid layout to display albums
            LinearLayout layout2 = new LinearLayout(this);
            layout2.setGravity(Gravity.CENTER_HORIZONTAL);
            GridLayout gridLayout = new GridLayout(this);
            //Add to UI
            layout.addView(layout2);
            layout2.addView(gridLayout);
            //Maximum nubmer of columns
            int columns = 2;
            for(int i = 300; i < width; i+= 150)
                columns++;
            columns--;
            gridLayout.setColumnCount(columns);
            //Loop through the array of albums pictures
            for (int i = 0; i < photoArray.length(); i++) {
                //Get the individual object
                JSONObject albumJSON = photoArray.getJSONObject(i);
                //If the album corresponds with the artist
                if(albumJSON.getInt("albumId") == albumID) {
                    //Retrieve the image from the link
                    Bitmap d = new RetrieveImage().execute("https://placeholdit.imgix.net/~text?txtsize=14&bg=" + albumJSON.getString("thumbnailUrl").substring(24) + "&txt=150%C3%97150&w=150&h=150").get();
                    //Create Image in the UI
                    ImageView imageView = new ImageView(this);
                    imageView.setImageBitmap(d);
                    //Set the ID so we know which image it is when clicked
                    imageView.setId(albumJSON.getInt("id"));
                    //Add to UI
                    gridLayout.addView(imageView);
                    //Action whenm image is clicked
                    imageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            //OPen larger image
                            viewIcon(v.getId(), albumID);
                        }
                    });
                    //Stop the loop early if have gone past artists albums
                } else if (albumJSON.getInt("albumId") > albumID)
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens up the thumbnail to dsiplay the full image
     * @param iconID ID of the thumbnail
     * @param artistID ID of the artist
     */
    void viewIcon(int iconID, final int artistID)
    {
        try {
            //Get the list of photos
            String jsonString = new RetrieveAlbumActivity().execute("http://jsonplaceholder.typicode.com/photos").get();
            //Put them in an array
            JSONArray imageArray = new JSONArray(jsonString);
            //Get the layout
            LinearLayout layout = (LinearLayout) findViewById(R.id.layout1);
            //Clear the UI
            layout.removeAllViews();
            //Create a back button
            Button butt = new Button(this);
            //Set Width
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            butt.setWidth(width);
            butt.setText("Go Back");
            //Action for back button
            butt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Go back to previous page (artist thumbanils)
                    generateAlbumCoverList(artistID);
                }
            });
            //add the button
            layout.addView(butt);
            //Loop through the array
            for (int i = 0; i < imageArray.length(); i++) {
                //Get the individual JSOn object
                JSONObject imageJson = imageArray.getJSONObject(i);
                //OFind corresponding image with ID
                if(imageJson.getInt("id") == iconID) {
                    //Retrieve the image from the server
                    Bitmap d = new RetrieveImage().execute("https://placeholdit.imgix.net/~text?txtsize=56&bg=" + imageJson.getString("thumbnailUrl").substring(24) + "&txt=600×600&w=600&h=600").get();
                    //Add image to ImageVIEW
                    ImageView imageView = new ImageView(this);
                    imageView.setImageBitmap(d);
                    //Add image to UI
                    layout.addView(imageView);
                    //Break loop once image is found
                }  else if (imageJson.getInt("id") > iconID)
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
