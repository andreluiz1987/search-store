package com.company.searchstore.mappers;

import com.company.searchstore.dto.MovieDTO;
import com.company.searchstore.dto.MovieMltDTO;
import com.company.searchstore.models.Movie;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {

  MovieDTO toDto(Movie movie);

  List<MovieMltDTO> toMltDtos(List<Movie> movies);
}
