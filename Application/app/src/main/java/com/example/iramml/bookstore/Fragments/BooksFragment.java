package com.example.iramml.bookstore.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.iramml.bookstore.BookStoreApi.BookStore;
import com.example.iramml.bookstore.Interfaces.getBooksInterface;
import com.example.iramml.bookstore.Model.BooksResponse;
import com.example.iramml.bookstore.R;
import com.example.iramml.bookstore.RecyclerViewBooks.BooksCustomAdapter;
import com.example.iramml.bookstore.RecyclerViewBooks.ClickListener;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 */
public class BooksFragment extends Fragment {
    View view0;
    ShimmerRecyclerView rvBooks;
    RecyclerView.LayoutManager layoutManager;
    BooksCustomAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    BookStore bookStore;
    AppCompatActivity appCompatActivity;

    BottomSheetBuyPDF bottomSheetBuyPDF;
    BottomSheetBuyPhysic bottomSheetBuyPhysic;
    public BooksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view0=inflater.inflate(R.layout.fragment_books, container, false);
        initRecyclerView();
        swipeRefreshLayout=view0.findViewById(R.id.swipeToRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initRecyclerView();
                bookStore.getBooks(new getBooksInterface() {
                    @Override
                    public void booksGenerated(String books) {
                        Gson gson=new Gson();
                        Log.d("RESPONSE", books);
                        BooksResponse booksObject=gson.fromJson(books, BooksResponse.class);
                        implementRecyclerView(booksObject);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        return view0;
    }
    public void hideBottomSheet(){
        if(bottomSheetBuyPhysic.isVisible()) bottomSheetBuyPhysic.dismiss();
        else bottomSheetBuyPDF.dismiss();
    }
    private void initRecyclerView() {
        rvBooks=(ShimmerRecyclerView)view0.findViewById(R.id.rvBooks);
        rvBooks.showShimmerAdapter();
        rvBooks.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getActivity());
        rvBooks.setLayoutManager(layoutManager);
    }
    public void setActivity(final AppCompatActivity appCompatActivity){
        this.appCompatActivity=appCompatActivity;
        bookStore=new BookStore(this.appCompatActivity);
        bottomSheetBuyPDF=BottomSheetBuyPDF.newInstance("PDF bottom sheet");
        bottomSheetBuyPhysic=BottomSheetBuyPhysic.newInstance("Physic bottom sheet");
        bookStore.getBooks(new getBooksInterface() {
            @Override
            public void booksGenerated(String books) {
                Gson gson=new Gson();
                Log.d("RESPONSE", books);
                BooksResponse booksObject=gson.fromJson(books, BooksResponse.class);
                if(booksObject.code.equals("200"))
                    implementRecyclerView(booksObject);
                else
                    Toast.makeText(appCompatActivity, "There are no books", Toast.LENGTH_SHORT).show();

            }
        });

    }
    public void implementRecyclerView(final BooksResponse booksObject){
        adapter=new BooksCustomAdapter(appCompatActivity, booksObject.books, new ClickListener() {
            @Override
            public void onClick(View view, int index) {
                if(booksObject.books.get(index).is_pdf.equals("yes")){
                    bottomSheetBuyPDF.setActivity(appCompatActivity, booksObject.books.get(index).title, booksObject.books.get(index).id, BooksFragment.this);
                    bottomSheetBuyPDF.show(getFragmentManager(), bottomSheetBuyPDF.mTag);
                }else{
                    bottomSheetBuyPhysic.setActivity(appCompatActivity, BooksFragment.this);
                    bottomSheetBuyPhysic.show(getFragmentManager(), bottomSheetBuyPhysic.mTag);
                }
            }
        });
        rvBooks.setAdapter(adapter);
    }
}
