package com.company.searchstore.core.fields;

public final class FieldAttr {

  private FieldAttr() {
  }

  public static class Movie {

    private Movie() {
    }

    public static final String TITLE_FIELD = "title";
    public static final String TITLE_SUGGEST = "title.suggest";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String ACTORS_SUGGEST = "actors";
    public static final String DIRECTOR_SUGGEST = "director";
  }

  public static class Suggest {

    private Suggest() {
    }

    public static final String TITLE_SUGGEST = "title_suggest";
    public static final String TITLE_SUGGEST_NAME = "title-suggest";
    public static final String DID_YOU_MEAN = "did_you_mean";
  }

  public static class Aggregations {

    private Aggregations() {
    }

    public static final String FACET_GENRE_NAME = "agg_genre";
    public static final String FACET_CERTIFICATE_NAME = "agg_certificate";
    public static final String FACET_GENRE = "genre.keyword";
    public static final String FACET_CERTIFICATE = "certificate";
  }
}
