package com.developer.abhinavraj.searchwidgetexample;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.developer.abhinavraj.searchwidgetexample.adapter.ClientSuggestionsAdapter;
import com.developer.abhinavraj.searchwidgetexample.models.GenericProductModel;
import com.developer.abhinavraj.searchwidgetexample.models.SearchItemModel;
import com.harsh.searchwidget.Builder.DefaultClientSuggestions;
import com.harsh.searchwidget.Fragments.VoicePermissionDialogFragment;
import com.harsh.searchwidget.Model.ClientSuggestionsModel;
import com.harsh.searchwidget.Model.SearchPropModel;
import com.harsh.searchwidget.SearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomFragment extends Fragment {


    SearchBar searchBar;
    //    ListView listView;
    RecyclerView recyclerView;
    ArrayList<ArrayList<String>> list;
    CategoryResultAdapter categoryResultAdapter;
    GridLayoutManager mGridLayoutManager;
    ArrayList<SearchItemModel> filteredData;
    ClientSuggestionsAdapter adapter;
    SearchPropModel searchPropModel;
    private SearchBar.ItemClickListener itemClickListener;
    //    String queryText;
    private ArrayList<String> dataFields;
    private ArrayList<Integer> weights;
    boolean closeSuggestions=false;
    private ArrayList<ClientSuggestionsModel> defaultSuggestions;
    int RecyclerViewItemPosition;
    View ChildView ;

    public CustomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);
        searchBar = (SearchBar) view.findViewById(R.id.search2);
//        listView = (ListView) findViewById(R.id.search_list_view);
        recyclerView = (RecyclerView) view.findViewById(R.id.search_list_view);

        searchBar.setAppbaseClient("https://scalr.api.appbase.io", "shopify-flipkart-test", "xJC6pHyMz", "54fabdda-4f7d-43c9-9960-66ff45d8d4cf", "products");

        // Setting basic search prop
        dataFields = new ArrayList<>();
        dataFields.add("title");
        dataFields.add("title.search");

        // Setting weights for dataFields
        weights = new ArrayList<>();
        weights.add(1);
        weights.add(3);

        // Making list of default suggestions
        ArrayList<String> suggestions = new ArrayList<>();
        suggestions.add("Puma T-Shirt");
        suggestions.add("Apple iPhone XS");
        suggestions.add("Nike Trousers");

        // Making list of default categories to be displayed
        final ArrayList<String> categories = new ArrayList<>();
        categories.add("T-Shirt");
        categories.add("Mobiles");

        // Setting default suggestions
        defaultSuggestions = new DefaultClientSuggestions(suggestions).setCategories(categories).build();

        searchBar.setPlaceHolderText("Search");
        searchBar.setMaxSuggestionCount(5);

        // Setting extra properties
        ArrayList<String> extraProperties = new ArrayList<>();
        extraProperties.add("image");

        searchPropModel = searchBar.setSearchProp("Demo Widget", dataFields)
                .setQueryFormat("or")
                .setHighlight(true)
                .setTopEntries(10)
                .setHitsEnabled(true)
                .setRedirectIcon(false)
                .setCategoryField("tags.keyword")
                .setInPlaceCategory(false)
                .setSearchResultImage(false)
                .setExtraFields(extraProperties)
                .build();

        ArrayList<String> entries = new ArrayList<>();
//        ArrayList<HashMap<String, ArrayList<String>>> temp2 = new ArrayList<>();

        ArrayList<ClientSuggestionsModel> adapterEntries;
        adapterEntries = new DefaultClientSuggestions(entries).build();
        adapter = new ClientSuggestionsAdapter(adapterEntries, "", searchPropModel.isHighlight(), true, searchPropModel.isSearchResultImage(), searchPropModel.isRedirectIcon());

        searchBar.getRecyclerView().setAdapter(adapter);

        itemClickListener = new SearchBar.ItemClickListener() {
            @Override
            public void onClick(View view, int position, ClientSuggestionsModel result) {
                try {
                    String response = searchBar.search(searchPropModel,String.valueOf(result.getText()));
                    Log.d("RESPONSE", response);

                Toast.makeText(getActivity(), result.getText(), Toast.LENGTH_SHORT).show();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            searchBar.clearSuggestions();
//                        }
//                    }, 5000);;
                    closeSuggestions = true;
                    searchBar.setText(result.getText());
                    closeSuggestions = true;
//
                    mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
                    recyclerView.setLayoutManager(mGridLayoutManager);

                    // Adding items to recycler view
                    list = new ArrayList<>();
                    AddItemsToRecycler addItemsToRecycler = new AddItemsToRecycler();
                    addItemsToRecycler.execute(response);

                    categoryResultAdapter = new CategoryResultAdapter(list);
                    recyclerView.setAdapter(categoryResultAdapter);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

//                }
            }

            @Override
            public void onLongClick(View view, int position, ClientSuggestionsModel result) {

            }
        };

        searchBar.startSearch(searchPropModel,itemClickListener);

        searchBar.setLoggingQuery(true);

        searchBar.setSpeechMode(true);


        searchBar.setOnTextChangeListener(new SearchBar.TextChangeListener() {
            @Override
            public void onTextChange(String response) {
                // Responses to the queries passed in the Search Bar are available here
                Log.d("Results", response);
                new StartSearching().execute(response, searchBar.getText(), itemClickListener);
            }
        });

        // To log the queries made by Appbase client for debugging

        // Managing voice recording permissions on record button click
        searchBar.setOnSearchActionListener(new SearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                try {
                    String response = searchBar.search(searchPropModel,String.valueOf(text));
                    Log.d("RESPONSE", response);
                    filteredData = new ArrayList<>();
                    searchBar.clearSuggestions();

                    mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
                    recyclerView.setLayoutManager(mGridLayoutManager);
                    closeSuggestions = true;
                    adapter.clear();
                    searchBar.getRecyclerView().setAdapter(adapter);

                    // Adding items to recycler view
                    list = new ArrayList<>();
                    AddItemsToRecycler addItemsToRecycler = new AddItemsToRecycler();
                    addItemsToRecycler.execute(response);

                    categoryResultAdapter = new CategoryResultAdapter(list);
                    recyclerView.setAdapter(categoryResultAdapter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if(buttonCode == SearchBar.BUTTON_SPEECH) {
                    if(searchBar.isVoicePermissionGranted()) {
                        searchBar.startVoiceSearch(searchPropModel, new SearchBar.ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, ClientSuggestionsModel result) {

                            }

                            @Override
                            public void onLongClick(View view, int position, ClientSuggestionsModel result) {

                            }
                        });
                    } else {
                        getFragmentManager().beginTransaction().add(new VoicePermissionDialogFragment(), "Recording Permission").commit();
                    }
                }
            }
        });
        return view;
    }

    String nullCheck(String check){
        return check == null ? "": check;
    }

    private class AddItemsToRecycler extends AsyncTask<String,Void,Void> {
        String result;
        @Override
        protected Void doInBackground(String... strings) {
            result = strings[0];
            try {

                JSONObject resultJSON = new JSONObject(result);
                JSONObject hits = resultJSON.getJSONObject("hits");
                JSONArray finalHits = hits.getJSONArray("hits");

                for (int i = 0; i < finalHits.length(); i++) {

                    JSONObject obj = finalHits.getJSONObject(i);
                    JSONObject source = obj.getJSONObject("_source");
                    String entry = source.getString("title");

                    //Log.d("FINAL HITS", entry);
                    String desc = source.getString("body_html");

                    JSONObject img = source.getJSONObject("image");
                    String url = img.getString("src");

                    float price = (new Random().nextInt(5000 - 500 + 1)) + 500;

                    ArrayList<String> arrayList = new ArrayList<>();
                    if(entry.length() > 40) {
                        String newEntry = entry.substring(0,40);
                        newEntry += "...";
                        entry = newEntry;
                    }
                    arrayList.add(entry);
                    arrayList.add("Rs. " + Math.round(price));
                    arrayList.add(url);
                    arrayList.add(desc);
                    list.add(arrayList);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class SetSearchBarText extends AsyncTask<String,Void,Void> {
        String text;
        @Override
        protected Void doInBackground(String... strings) {
            text = strings[0];
            setText(text);
            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            searchBar.clearSuggestions();
        }
    }

    void setText(final String text){
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // Any UI task, example
                searchBar.setText(text);
            }
        };
        handler.sendEmptyMessage(1);
    }

    private class StartSearching extends AsyncTask<Object,Void,Void> {
        String result;
        ArrayList<String> entries;
        ArrayList<String> duplicateCheck;
        String query;
        ArrayList<String> categories;
        ArrayList<HashMap<String, ArrayList<String>>> extraProperties;
        SearchBar.ItemClickListener itemClickListener;
        @Override
        protected Void doInBackground(Object... params) {
            result = (String) params[0];
            query = (String) params[1];
            itemClickListener = (SearchBar.ItemClickListener) params[2];

            try {

                JSONObject resultJSON = new JSONObject(result);
                JSONObject hits = resultJSON.getJSONObject("hits");
                JSONArray finalHits = hits.getJSONArray("hits");

                entries = new ArrayList<>();
                categories = new ArrayList<>();
                duplicateCheck = new ArrayList<>();
                extraProperties = new ArrayList<>();
                for (int i = 0; i < finalHits.length(); i++) {


                    JSONObject obj = finalHits.getJSONObject(i);
                    JSONObject source = obj.getJSONObject("_source");

                    for(int j = 0; j < searchPropModel.getDataField().size(); j++) {

                        try {
                            String entry = source.getString(searchPropModel.getDataField().get(j));
                            if(!duplicateCheck.contains(entry.toLowerCase())) {
                                entries.add(entry);
                                duplicateCheck.add(entry.toLowerCase());
                            }
                        } catch (JSONException e) {
                            // Error finding data field
                        }

                    }

                    if(searchPropModel.getCategoryField() != null) {
                        try {
                            JSONArray categoryArray = source.getJSONArray(searchPropModel.getCategoryField());
                            categories.add(categoryArray.get(0).toString());
                        } catch (JSONException e) {

                            try {
                                String category = source.getString(searchPropModel.getCategoryField());
                                categories.add(category);
                            } catch (JSONException err) {
                                //err.printStackTrace();
                            }
                        }

                    }

                    if(searchPropModel.getExtraFields() != null) {

                        for(int j = 0; j < searchPropModel.getExtraFields().size(); j++) {

                            try {
                                JSONArray extraFieldsArray = source.getJSONArray(searchPropModel.getExtraFields().get(j));

                                ArrayList<String> tempProperty = new ArrayList<>();
                                for(int k = 0; k < extraFieldsArray.length(); k++) {
                                    JSONObject category = extraFieldsArray.getJSONObject(i);
                                    tempProperty.add(category.toString());
                                }

                                HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
                                hashMap.put(searchPropModel.getExtraFields().get(j), tempProperty);
                                extraProperties.add(hashMap);

                            } catch (JSONException e) {

                                try {
                                    String extraField = source.getString(searchPropModel.getExtraFields().get(j));

                                    ArrayList<String> tempProperty = new ArrayList<>();
                                    tempProperty.add(extraField);
                                    HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
                                    hashMap.put(searchPropModel.getExtraFields().get(j), tempProperty);
                                    extraProperties.add(hashMap);

                                } catch (JSONException err) {
                                    //err.printStackTrace();
                                }
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(searchPropModel.isAutoSuggest()) {
                ArrayList<ClientSuggestionsModel> adapterEntries;
                if(searchPropModel.getCategoryField() != null && categories != null) {
                    adapterEntries = new DefaultClientSuggestions(entries).setCategories(categories).setExtraProperties(extraProperties).build();
                    adapter = new ClientSuggestionsAdapter(adapterEntries, query, searchPropModel.isHighlight(), true, searchPropModel.isSearchResultImage(), searchPropModel.isRedirectIcon(), searchPropModel.getTopEntries());
                }
                else {
                    adapterEntries = new DefaultClientSuggestions(entries).build();
                    adapter = new ClientSuggestionsAdapter(adapterEntries, query, searchPropModel.isHighlight(), true, searchPropModel.isSearchResultImage(), searchPropModel.isRedirectIcon());
                }

                adapter.setRecyclerItemClickListener(new ClientSuggestionsAdapter.RecyclerItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        itemClickListener.onClick(v, position, adapter.getItem(position));
                    }

                    @Override
                    public void onItemClickLong(View v, int position) {
                        itemClickListener.onLongClick(v, position, adapter.getItem(position));
                    }
                });

                if(closeSuggestions) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    closeSuggestions = false;
                }
//                for(int i = 0; i < adapter.getItemCount(); i++)
//                    Log.d("HIII", adapter.getItem(i).getText());
                searchBar.getRecyclerView().setVisibility(View.VISIBLE);
                searchBar.getRecyclerView().setAdapter(adapter);
            }

        }
    }


}
