package com.jerry.empty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    TextView displayArea;
    TextView brandSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayArea = findViewById(R.id.textView4);
        brandSearch = findViewById(R.id.editText2);
        db = FirebaseFirestore.getInstance();
    }


    public void searchByBrand(View button)  {
        displayArea.setText("searching............");
        DocumentReference brand = db.collection("brands").document(brandSearch.getText().toString());
        brand.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)  {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    StringBuilder fields = null;

                    String meta_description = null;
                    try {
                        meta_description = getAttribute( new StringBuilder("").append(result.get("meta_description")).toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }

                        fields = new StringBuilder("")
                                .append(getColoredSpanned(new StringBuilder("").append("<h1>1. h1: </h1>" + result.get("h1")).toString(),"#000080"))
                                .append(getColoredSpanned(new StringBuilder("").append("<h1>2. h2: </h1>" + result.get("h2")).toString(),"#800080"))
                        .append(getColoredSpanned(new StringBuilder("").append("<h1>3. meta description: </h1>" + meta_description).toString(),"#800080"));


                    displayArea.setMovementMethod(new ScrollingMovementMethod());
                    displayArea.setText(Html.fromHtml(fields.toString()));
//                    addDisplayContent("1.h1:",result.get("h1").toString());
//                    addDisplayContent("2.h2:",result.get("h2").toString());

//                    StringBuilder fields = new StringBuilder("").append("h1: " + result.get("h1")).append("\nh2: " + result.get("h2"));
//
//                    displayArea.setText(fields.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    private void addDisplayContent(String section, String content){
        Spannable word = new SpannableString(section);

        word.setSpan(new ForegroundColorSpan(Color.BLUE), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        displayArea.setText(word);
        Spannable wordTwo = new SpannableString(content);

        wordTwo.setSpan(new ForegroundColorSpan(Color.WHITE), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        displayArea.append(wordTwo);
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    private String getAttribute(String xmlString) throws IOException, SAXException, ParserConfigurationException {

        Document document = parseXmlFromString(xmlString);
        NodeList nodeList = document.getElementsByTagName("meta");

        return nodeList.item(0).getAttributes().getNamedItem("content").getNodeValue();

    }

    private Document parseXmlFromString(String xmlString) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream inputStream = new    ByteArrayInputStream(xmlString.getBytes());
        org.w3c.dom.Document document = builder.parse(inputStream);
        return document;
    }
}
