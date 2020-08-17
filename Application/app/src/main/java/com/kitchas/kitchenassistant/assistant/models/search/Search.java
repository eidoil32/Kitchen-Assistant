package com.kitchas.kitchenassistant.assistant.models.search;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kitchas.kitchenassistant.assistant.Base;
import com.kitchas.kitchenassistant.utils.callbacks.IErrorCallback;
import com.kitchas.kitchenassistant.utils.database.SQLHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Search extends Base {
    public static int MAX_SEARCH_HISTORY = 10;
    public static String DB_IDENTIFY = "LAST_SEARCH";
    private Context context;

    public Search(Context context) {
        this.context = context;
    }

    public static List<String> loadSearchSuggestionFromDisk(Context context) {
        List<String> lastResults = new LinkedList<>();

        SQLHelper database = new SQLHelper(context);
        String json = database.getData(DB_IDENTIFY);
        Type listType = new TypeToken<List<String>>() {}.getType();
        if (json != null) {
            lastResults = new Gson().fromJson(json, listType);
        }

        return lastResults;
    }

    public void searchInUserRecipe(String query, boolean include_favorites, ISearchCallback success_callback, IErrorCallback error_callback) {
        // TODO implement searchFor function:
        // API endpoint: "user/recipes/search" in GET
        // 1. process the query
        // 2. create Map<String, String> of the following parameters:
        //      2.1. query (query)
        //      2.2. favorite (include_favorites)
        // 3. Send query and callbacks to Server with HTTPManager.GET
    }

    public void searchInCommunity(String query, ISearchCallback success_callback, IErrorCallback error_callback) {
        // TODO implement searchFor function:
        // API endpoint: "community/search" in GET
        // 1. process the query
        // 2. create Map<String, String> of the following parameters:
        //      2.1. query (query)
        // 3. Send query and callbacks to Server with HTTPManager.GET
    }

    public void saveSearch(String query, List searches) {
        if (!searches.contains(query)) {
            if (searches.size() > MAX_SEARCH_HISTORY) {
                searches.remove(0);
            }

            searches.add(query);
            System.out.println(Arrays.toString(searches.toArray()));
            SQLHelper database = new SQLHelper(this.context);
            if (!database.updateData(searches, DB_IDENTIFY)) {
                database.insertData(searches, DB_IDENTIFY);
            }
        }
    }
}