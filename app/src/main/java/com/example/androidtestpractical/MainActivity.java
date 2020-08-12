package com.example.androidtestpractical;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.example.androidtestpractical.adapters.MyAdapter;
import com.example.androidtestpractical.model.Item;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Item> myLeftList;
    private ArrayList<Item> myRightList;

    @BindView(R.id.etInput)
    AppCompatEditText etInput;

    @BindView(R.id.btnAdd)
    AppCompatTextView btnAdd;

    @BindView(R.id.btnDelete)
    AppCompatTextView btnDelete;

    @BindView(R.id.recyclerLeft)
    RecyclerView recyclerLeft;

    @BindView(R.id.recyclerRight)
    RecyclerView recyclerRight;

    @BindView(R.id.btnCopyL2R)
    AppCompatTextView btnCopyL2R;

    @BindView(R.id.btnCopyR2L)
    AppCompatTextView btnCopyR2L;

    @BindView(R.id.btnMoveL2R)
    AppCompatTextView btnMoveL2R;

    @BindView(R.id.btnMoveR2L)
    AppCompatTextView btnMoveR2L;

    @BindView(R.id.btnSwap)
    AppCompatTextView btnSwap;

    private MyAdapter myLeftAdapter;
    private MyAdapter myRightAdapter;

    private ArrayList<Item> myLeftSelecteItems = new ArrayList<>();
    private ArrayList<Item> myRightSelecteItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        btnAdd.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnCopyL2R.setOnClickListener(this);
        btnCopyR2L.setOnClickListener(this);
        btnMoveL2R.setOnClickListener(this);
        btnMoveR2L.setOnClickListener(this);
        btnSwap.setOnClickListener(this);
    }

    private void initView() {
        myLeftList = new ArrayList<>();
        recyclerLeft.setLayoutManager(new LinearLayoutManager(this));
        myLeftAdapter = new MyAdapter(myLeftList);
        recyclerLeft.setAdapter(myLeftAdapter);
        myLeftAdapter.setMyViewCheckChangeListener(new MyViewCheckChangeListener() {
            @Override
            public void checkChanged(Item item) {
                if (item.isChecked())
                    myLeftSelecteItems.add(item);
                else
                    myLeftSelecteItems.remove(item);
            }
        });

        myRightList = new ArrayList<>();
        recyclerRight.setLayoutManager(new LinearLayoutManager(this));
        myRightAdapter = new MyAdapter(myRightList);
        recyclerRight.setAdapter(myRightAdapter);
        myRightAdapter.setMyViewCheckChangeListener(new MyViewCheckChangeListener() {
            @Override
            public void checkChanged(Item item) {
                if (myRightSelecteItems == null)
                    myRightSelecteItems = new ArrayList<>();
                if (item.isChecked())
                    myRightSelecteItems.add(item);
                else
                    myRightSelecteItems.remove(item);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                if (etInput.getText() != null && !TextUtils.isEmpty(etInput.getText().toString().trim())) {
                    String myInputString = etInput.getText() != null ? etInput.getText().toString() : "";
                    if (!Stream.of(myLeftList).anyMatch(v -> v.getName().equalsIgnoreCase(myInputString))) {
                        myLeftList.add(new Item(myInputString, false));
                        myLeftAdapter.notifyDataSetChanged();
                        etInput.setText("");
                    } else {
                        showSnackBar(myInputString + " is already in List");
                    }
                } else {
                    showSnackBar("please fill Value in Input Box");
                }
                break;

            case R.id.btnDelete:
                String myInputString = etInput.getText() != null ? etInput.getText().toString() : "";
                if (etInput.getText() != null && !TextUtils.isEmpty(etInput.getText().toString())) {
                    if (Stream.of(myLeftList).anyMatch(v -> v.getName().equalsIgnoreCase(myInputString))) {
                        Item item =
                                Stream.of(myLeftList).filter(v -> v.getName()
                                        .equalsIgnoreCase(myInputString)).findFirst().get();
                        myLeftAdapter.notifyItemRemoved(myLeftList.indexOf(item));
                        myLeftList.remove(item);
                    }
                } else {
                    showSnackBar(myInputString + " is not present in List");
                }
                break;

            case R.id.btnCopyL2R:
                if (myLeftSelecteItems.size() == 0) {
                    showSnackBar("please select atleast one element from Left List");
                } else {
                    copyListL2R(myLeftSelecteItems);
                }
                break;

            case R.id.btnCopyR2L:
                if (myRightSelecteItems.size() == 0) {
                    showSnackBar("please select atleast one element from Right List");
                } else {
                    copyListR2L(myRightSelecteItems);
                }
                break;

            case R.id.btnMoveL2R:
                if (myLeftSelecteItems.size() == 0) {
                    showSnackBar("please select atleast one element from Left List");
                } else {
                    moveListL2R(myLeftSelecteItems);
                }
                break;

            case R.id.btnMoveR2L:
                if (myRightSelecteItems.size() == 0) {
                    showSnackBar("please select atleast one element from Right List");
                } else {
                    moveListR2L(myRightSelecteItems);
                }
                break;

            case R.id.btnSwap:
                if (myRightSelecteItems != null) {
                    if (myLeftSelecteItems.size() == 0 && myRightSelecteItems.size() == 0) {
                        showSnackBar("please select one item from both list");
                    } else {
                        if (myLeftSelecteItems.size() == myRightSelecteItems.size())
                            swap();
                        else
                            showSnackBar("please select same item size in both list");
                    }
                } else
                    showSnackBar("please select same item size in both list");
                break;
        }
    }

    private void moveListR2L(ArrayList<Item> myMoveList) {
        if (myLeftList.size() == 0) {
            for (Item item : myMoveList) {
                item.setChecked(false);
            }
            myLeftList.addAll(myMoveList);
            myLeftAdapter.notifyDataSetChanged();

            for (int i = 0; i < myMoveList.size(); i++) {
                myRightList.remove(myMoveList.get(i));
            }
            myRightAdapter.notifyDataSetChanged();
            myRightSelecteItems = new ArrayList<>();
        } else {
            for (Item item : myLeftList) {
                item.setChecked(false);
            }
            ArrayList<Item> myList = new ArrayList<>();
            for (int i = 0; i < myMoveList.size(); i++) {
                myMoveList.get(i).setChecked(false);
                String name = myMoveList.get(i).getName();
                if (!Stream.of(myLeftList)
                        .anyMatch(v -> v.getName().equalsIgnoreCase(name))) {
                    myList.add(myMoveList.get(i));
                }
                /*if (!myLeftList.contains(myMoveList.get(i))) {
                    myList.add(myMoveList.get(i));
                }*/
            }
            myLeftList.addAll(myList);
            myLeftAdapter.notifyDataSetChanged();
            for (int i = 0; i < myMoveList.size(); i++) {
                myRightList.remove(myMoveList.get(i));
            }
            myRightAdapter.notifyDataSetChanged();
            myRightSelecteItems = new ArrayList<>();
        }
    }

    private void moveListL2R(ArrayList<Item> myMoveList) {
        if (myRightList.size() == 0) {
            for (Item item : myMoveList) {
                item.setChecked(false);
            }
            myRightList.addAll(myMoveList);
            myRightAdapter.notifyDataSetChanged();

            for (int i = 0; i < myMoveList.size(); i++) {
                myLeftList.remove(myMoveList.get(i));
            }
            myLeftAdapter.notifyDataSetChanged();
            myLeftSelecteItems = new ArrayList<>();
        } else {
            for (Item item : myRightList) {
                item.setChecked(false);
            }
            ArrayList<Item> myList = new ArrayList<>();
            for (int i = 0; i < myMoveList.size(); i++) {
                myMoveList.get(i).setChecked(false);
                String name = myMoveList.get(i).getName();
                if (!Stream.of(myRightList)
                        .anyMatch(v -> v.getName().equalsIgnoreCase(name))) {
                    myList.add(myMoveList.get(i));
                }
                /*if (!myRightList.contains(myMoveList.get(i))) {
                    myList.add(myMoveList.get(i));
                }*/
            }
            myRightList.addAll(myList);
            myRightAdapter.notifyDataSetChanged();
            for (int i = 0; i < myMoveList.size(); i++) {
                myLeftList.remove(myMoveList.get(i));
            }
            myLeftAdapter.notifyDataSetChanged();
            myLeftSelecteItems = new ArrayList<>();
        }
    }

    private void copyListL2R(ArrayList<Item> myCopyList) {
        if (myRightList.size() == 0) {
            for (Item item : myCopyList) {
                item.setChecked(false);
            }
            myRightList.addAll(myCopyList);
            myRightAdapter.notifyDataSetChanged();
            for (Item item : myLeftList) {
                item.setChecked(false);
            }
            myLeftAdapter.notifyDataSetChanged();
            myLeftSelecteItems = new ArrayList<>();
        } else {
            for (Item item : myRightList) {
                item.setChecked(false);
            }
            ArrayList<Item> myList = new ArrayList<>();
            for (int i = 0; i < myCopyList.size(); i++) {
                myCopyList.get(i).setChecked(false);
                String name = myCopyList.get(i).getName();
                if (!Stream.of(myRightList)
                        .anyMatch(v -> v.getName().equalsIgnoreCase(name))) {
                    myList.add(myCopyList.get(i));
                }
                /*if (!myRightList.contains(myCopyList.get(i))) {
                    myList.add(myCopyList.get(i));
                }*/
            }
            myRightList.addAll(myList);
            myRightAdapter.notifyDataSetChanged();
            for (Item item : myLeftList) {
                item.setChecked(false);
            }
            myLeftAdapter.notifyDataSetChanged();
            myLeftSelecteItems = new ArrayList<>();
        }
    }

    private void copyListR2L(ArrayList<Item> myCopyList) {
        if (myLeftList.size() == 0) {
            for (Item item : myCopyList) {
                item.setChecked(false);
            }
            myLeftList.addAll(myCopyList);
            myLeftAdapter.notifyDataSetChanged();
            for (Item item : myRightList) {
                item.setChecked(false);
            }
            myRightAdapter.notifyDataSetChanged();
            myRightSelecteItems = new ArrayList<>();
        } else {
            for (Item item : myLeftList) {
                item.setChecked(false);
            }
            ArrayList<Item> myList = new ArrayList<>();
            for (int i = 0; i < myCopyList.size(); i++) {
                myCopyList.get(i).setChecked(false);
                String name = myCopyList.get(i).getName();
                if (!Stream.of(myLeftList)
                        .anyMatch(v -> v.getName().equalsIgnoreCase(name))) {
                    myList.add(myCopyList.get(i));
                }
                /*if (!myLeftList.contains(myCopyList.get(i)))
                    myList.add(myCopyList.get(i));*/
            }
            myLeftList.addAll(myList);
            myLeftAdapter.notifyDataSetChanged();
            for (Item item : myRightList) {
                item.setChecked(false);
            }
            myRightAdapter.notifyDataSetChanged();
            myRightSelecteItems = new ArrayList<>();
        }
    }

    private void swap() {
        ArrayList<Integer> leftIndex = new ArrayList<>();
        Stream.of(myLeftList).forEach(new Consumer<Item>() {
            @Override
            public void accept(Item item) {
                if (item.isChecked())
                    leftIndex.add(myLeftList.indexOf(item));
            }
        });

        ArrayList<Integer> rightIndex = new ArrayList<>();
        Stream.of(myRightList).forEach(new Consumer<Item>() {
            @Override
            public void accept(Item item) {
                if (item.isChecked())
                    rightIndex.add(myRightList.indexOf(item));
            }
        });

        ArrayList<Item> myOldRightList = new ArrayList<>();
        for (int i = 0; i < leftIndex.size(); i++) {
            myOldRightList.add(myRightList.get(rightIndex.get(i)));
            myLeftList.get(leftIndex.get(i)).setChecked(false);
            myRightList.set(rightIndex.get(i), myLeftList.get(leftIndex.get(i)));
        }
        myRightAdapter.notifyDataSetChanged();

        for (int i = 0; i < rightIndex.size(); i++) {
            myOldRightList.get(i).setChecked(false);
            myLeftList.set(leftIndex.get(i), myOldRightList.get(i));
        }
        myLeftAdapter.notifyDataSetChanged();
    }


    private void showSnackBar(String msg) {
        View view1 = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(view1, msg, Snackbar.LENGTH_LONG);
        View myCustomView = snackbar.getView();
        TextView textView = (TextView) myCustomView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(15f);
        textView.setBackgroundColor(Color.BLACK);
        textView.setTextColor(Color.RED);
        snackbar.show();
    }
}