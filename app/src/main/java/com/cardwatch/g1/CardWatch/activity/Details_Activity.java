package com.cardwatch.g1.CardWatch.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cardwatch.g1.CardWatch.R;
import com.cardwatch.g1.CardWatch.utils.Ressources;


/**
 * Réalisé par nicolassalleron le 11/05/16.
 */
public class Details_Activity extends Activity {

    //the images to display

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String title = getIntent().getStringExtra("title");
        TextView titleTextView = (TextView) findViewById(R.id.title);
        ImageView imageView = (ImageView) findViewById(R.id.image);

        if(title.split(";")[0].equals("1")){    //ImageInterneEtExterne
            titleTextView.setText(title.substring(2));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), Ressources.imageExterneEtInterne[Integer.parseInt(title.split("#")[1])]);
            imageView.setImageBitmap(bitmap);

        }else if(title.split(";")[0].equals("2")){  //ImageInterne
            titleTextView.setText(title.substring(2));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), Ressources.imageInterne[Integer.parseInt(title.split("#")[1])]);
            imageView.setImageBitmap(bitmap);

        }else if(title.split(";")[0].equals("3")){  //ImageSombre
            titleTextView.setText(title.substring(2));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), Ressources.imageSombreInterne[Integer.parseInt(title.split("#")[1])]);
            imageView.setImageBitmap(bitmap);

        }else if(title.split(";")[0].equals("4")){  //ImageFlash
            titleTextView.setText(title.substring(2));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), Ressources.imageInterneFlash[Integer.parseInt(title.split("#")[1])]);
            imageView.setImageBitmap(bitmap);

        }else if(title.split(";")[0].equals("5")){  //ImageExterne
            titleTextView.setText(title.substring(2));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), Ressources.imageExterne[Integer.parseInt(title.split("#")[1])]);
            imageView.setImageBitmap(bitmap);

        }








    }

}