package com.projeto.praticaapi.model

data class ItemDetailsModel(val poster: String,
                            val title: String,
                            val released: String,
                            val type: String,
                            val year: String,
                            val rated: String,
                            val runtime: String,
                            val genre: String,
                            val desc: String,
                            val creator: String,
                            val cast: String,
                            val awards: String,
                            val metascore: String,
                            val imdbRating: String,
                            val votes: String,
                            val ratings: List<RatingsModel>)