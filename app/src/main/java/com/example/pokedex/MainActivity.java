package com.example.pokedex;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.StringJoiner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText pokemonNameInput;
    private Button searchButton;
    private ImageView pokemonSprite;
    private TextView pokemonNameText;
    private TextView pokemonDetailsText;
    private TextView pokemonStatsText;

    private PokeApiService pokeApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pokemonNameInput = findViewById(R.id.pokemon_name_input);
        searchButton = findViewById(R.id.search_button);
        pokemonSprite = findViewById(R.id.pokemon_sprite);
        pokemonNameText = findViewById(R.id.pokemon_name_text);
        pokemonDetailsText = findViewById(R.id.pokemon_details_text);
        pokemonStatsText = findViewById(R.id.pokemon_stats_text);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        pokeApiService = retrofit.create(PokeApiService.class);

        searchButton.setOnClickListener(v -> performSearch());

        pokemonNameInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String pokemonName = pokemonNameInput.getText().toString().toLowerCase().trim();
        if (!pokemonName.isEmpty()) {
            searchPokemon(pokemonName);
            hideKeyboard();
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void searchPokemon(String pokemonName) {
        pokeApiService.getPokemon(pokemonName).enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful()) {
                    Pokemon pokemon = response.body();
                    if (pokemon != null) {
                        pokemonNameText.setText(pokemon.getName().substring(0, 1).toUpperCase() + pokemon.getName().substring(1));

                        // Set details
                        StringJoiner types = new StringJoiner(", ");
                        for (Pokemon.TypeInfo typeInfo : pokemon.getTypes()) {
                            types.add(typeInfo.getType().getName());
                        }
                        String details = "Type: " + types.toString() + "\n" +
                                "Height: " + pokemon.getHeight() / 10.0 + "m\n" +
                                "Weight: " + pokemon.getWeight() / 10.0 + "kg";
                        pokemonDetailsText.setText(details);

                        // Set stats
                        StringBuilder statsBuilder = new StringBuilder();
                        for (Pokemon.StatInfo statInfo : pokemon.getStats()) {
                            statsBuilder.append(statInfo.getStat().getName()).append(": ").append(statInfo.getBaseStat()).append("\n");
                        }
                        pokemonStatsText.setText(statsBuilder.toString());

                        Glide.with(MainActivity.this)
                                .load(pokemon.getSprites().getFrontDefault())
                                .into(pokemonSprite);
                    }
                } else {
                    pokemonNameText.setText("Pokemon not found");
                    pokemonDetailsText.setText("");
                    pokemonStatsText.setText("");
                    pokemonSprite.setImageResource(0); // Clear image
                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                pokemonNameText.setText("Error");
                pokemonDetailsText.setText(t.getMessage());
                pokemonStatsText.setText("");
                pokemonSprite.setImageResource(0); // Clear image
            }
        });
    }
}