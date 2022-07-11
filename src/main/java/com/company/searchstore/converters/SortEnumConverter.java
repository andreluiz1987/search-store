package com.company.searchstore.converters;

import com.company.searchstore.core.sort.SortEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SortEnumConverter implements Converter<String, SortEnum> {

  @Override
  public SortEnum convert(String value) {
    return SortEnum.valueOf(value.toUpperCase());
  }
}