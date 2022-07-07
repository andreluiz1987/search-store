package com.company.searchstore.core.fields;

public final class FieldAttr {

  private FieldAttr() {
  }

  public static class Movie {

    private Movie() {
    }

    public static final String TITLE_FIELD = "title";
    public static final String TITLE_SUGGEST = "title.suggest";
  }

  public static class Suggest {

    private Suggest() {
    }

    public static final String TITLE_SUGGEST = "title_suggest";
    public static final String TITLE_SUGGEST_NAME = "title-suggest";
    public static final String DID_YOU_MEAN = "did_you_mean";
  }
}
