package activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.vishwashrisairm.materialdesign.R;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

import java.text.Normalizer;
import java.util.List;

import adapter.EduRecyclerViewAdapter;
import adapter.HomeRecyclerViewAdapter;
import database.EduInfo;
import database.ItemStatus;
import helper.PInfoDbHandler;

/**
 * Created by vishwashrisairm on 22/3/16.
 */
public class FormEdu extends AppCompatActivity {
    private RecyclerView eduRecyclerView;
    private RecyclerView.Adapter eduAdapter;
    private RecyclerView.LayoutManager eduLayoutManager;
    private List<EduInfo> mItems;
    private int item_id;
    private ImageButton btnback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_edu);


        item_id=getIntent().getIntExtra("item_id",0);

        eduRecyclerView=(RecyclerView)findViewById(R.id.eduRecyclerView);
        eduRecyclerView.setHasFixedSize(true);
        eduLayoutManager=new LinearLayoutManager(this);
        eduRecyclerView.setLayoutManager(eduLayoutManager);
        mItems=getDataSet();
        eduAdapter=new EduRecyclerViewAdapter(mItems);
        eduRecyclerView.setAdapter(eduAdapter);


        btnback = (ImageButton) findViewById(R.id.btn_back_edu);

//        Swipe Touch Listener
        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(eduRecyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
//                                    Toast.makeText(MainActivity.this, mItems.get(position) + " swiped left", Toast.LENGTH_SHORT).show();

                                    EduInfo eduid=mItems.get(position);
                                    mItems.remove(position);
                                    PInfoDbHandler db = new PInfoDbHandler(recyclerView.getContext(),"",null,1);
                                    db.deleteEInfo(eduid);
                                    eduAdapter.notifyItemRemoved(position);
                                }
                                eduAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
//                                    Toast.makeText(MainActivity.this, mItems.get(position) + " swiped right", Toast.LENGTH_SHORT).show();
                                    EduInfo eduid=mItems.get(position);
                                    mItems.remove(position);
                                    PInfoDbHandler db = new PInfoDbHandler(recyclerView.getContext(),"",null,1);
                                    db.deleteEInfo(eduid);

                                    eduAdapter.notifyItemRemoved(position);
                                }
                                eduAdapter.notifyDataSetChanged();
                            }
                        });

        eduRecyclerView.addOnItemTouchListener(swipeTouchListener);

//        Floating action button
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_edu);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FormEdu.this);
                builder.setTitle("Education Information");

                // Set up the input
                final EditText edu_name = new EditText(FormEdu.this);
                final EditText  yop= new EditText(FormEdu.this);
                final EditText cgpa = new EditText(FormEdu.this);
                final EditText institute = new EditText(FormEdu.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                edu_name.setInputType(InputType.TYPE_CLASS_TEXT);
                edu_name.setHint("Examination");
                yop.setInputType(InputType.TYPE_CLASS_TEXT);
                yop.setHint("Year of Passing");
                cgpa.setInputType(InputType.TYPE_CLASS_TEXT);
                cgpa.setHint("CGPA/%age");
                institute.setInputType(InputType.TYPE_CLASS_TEXT);
                institute.setHint("Institute");

                LinearLayout ll=new LinearLayout(FormEdu.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(edu_name);
                ll.addView(yop);
                ll.addView(cgpa);
                ll.addView(institute);


                builder.setView(ll);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String edu = edu_name.getText().toString();
                        String year = yop.getText().toString();
                        String cg = cgpa.getText().toString();
                        String ins = institute.getText().toString();


                        PInfoDbHandler db = new PInfoDbHandler(FormEdu.this,"",null,1);
                        EduInfo e=new EduInfo(item_id, edu, year, cg, ins);
                        db.addEInfo(e);


                        Intent i = new Intent(FormEdu.this,FormEdu.class);
                        i.putExtra("item_id",item_id);
                        startActivity(i);
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


            }
        });


        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormEdu.this,FormPersonal.class);
                intent.putExtra("item_id",item_id);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        ((EduRecyclerViewAdapter) eduAdapter).setOnItemClickListener(new EduRecyclerViewAdapter.MyClickListener(){

            @Override
            public void onItemClick(int position, View v) {
                Log.i("edu", " Clicked on Item " + position);
            }
        });

    }

    private List<EduInfo> getDataSet() {
        PInfoDbHandler db = new PInfoDbHandler(this,"",null,1);
        List<EduInfo> i = db.getAllEInfoByID(item_id);
        for (EduInfo item : i) {
            String log = "Degree: " + item.get_degree() + " ,CGPA: " + item.get_cgpa() ;
            // Writing Contacts to log
            Log.d("Edu: ", log);
        }

        return i;
    }

    public void submitForm(View view) {
        PInfoDbHandler db = new PInfoDbHandler(FormEdu.this,"",null,1);

        if(mItems.size()>0)
        {
            db.updateStatusEdu(item_id,1);
        }
        else
            db.updateStatusEdu(item_id,0);

        db = new PInfoDbHandler(FormEdu.this,"",null,1);
        int c = db.getEInfoCountById(item_id);
        if(c>0) {
            Intent intent = new Intent(FormEdu.this, FormPro.class);
            intent.putExtra("item_id", item_id);
            startActivity(intent);
            finish();
        }
    }
}
