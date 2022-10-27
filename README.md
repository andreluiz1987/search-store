# API Search Store

# Search Store APP

This project represents the back-end api responsible for performing search queries in Elasticsearch.
This API provider features:

* Default Search
* Autocomplete Suggester
* Filter and Facets
* More Like This

## Technologies

* SpringBooot
* Java 11
* Elasticsearch
* TestContainers

## Pre-requisites

### Run docker-compose

````
docker-compose up -d
````

### Create Mapping

Access Dev-Tools and create mapping.

````
{
  "settings": {
    "analysis": {
      "analyzer": {
        "shingle_analyzer": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "shingle_filter"
          ]
        }
      },
      "filter": {
        "shingle_filter": {
          "type": "shingle",
          "min_shingle_size": 2,
          "max_shingle_size": 3
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "actors": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "date": {
        "type": "keyword",
        "doc_values": false
      },
      "description": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "director": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "genre": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "metascore": {
        "type": "long"
      },
      "rating": {
        "type": "float"
      },
      "revenue": {
        "type": "float"
      },
      "runtime": {
        "type": "long"
      },
      "title": {
        "type": "text",
        "fields": {
          "suggest": {
            "type": "text",
            "analyzer": "shingle_analyzer"
          }
        }
      },
      "title_suggest": {
        "type": "completion",
        "analyzer": "simple",
        "preserve_separators": true,
        "preserve_position_increments": true,
        "max_input_length": 50
      },
      "votes": {
        "type": "long"
      },
      "year": {
        "type": "long"
      }
    }
  }
}
````

### Execute Bulk Documents

For populate index, execute bulk insert with [dataset](../documents/dataset.txt).

````
POST idx_movies/_bulk
{"index": {}}
{"code": 1, "title": "The Shawshank Redemption", "title_suggest": ["The", "Shawshank", "Redemption"], "genre": ["Drama"], "director": "Frank Darabont", "actors": ["Tim Robbins", "Morgan Freeman", "Bob Gunton", "William Sadler"], "description": "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.", "year": 1994, "runtime": "142 min", "rating": 9.3, "votes": 2343110, "revenue": "28,341,469", "metascore": 80, "certificate": "A", "avatar": "https://m.media-amazon.com/images/M/MV5BMDFkYTc0MGEtZmNhMC00ZDIzLWFmNTEtODM1ZmRlYWMwMWFmXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_UX67_CR0,0,67,98_AL_.jpg"}
{"index": {}}
{"code": 2, "title": "The Godfather", "title_suggest": ["The", "Godfather"], "genre": ["Crime", "Drama"], "director": "Francis Ford Coppola", "actors": ["Marlon Brando", "Al Pacino", "James Caan", "Diane Keaton"], "description": "An organized crime dynasty's aging patriarch transfers control of his clandestine empire to his reluctant son.", "year": 1972, "runtime": "175 min", "rating": 9.2, "votes": 1620367, "revenue": "134,966,411", "metascore": 100, "certificate": "A", "avatar": "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_UY98_CR1,0,67,98_AL_.jpg"}
{"index": {}}
{"code": 3, "title": "The Dark Knight", "title_suggest": ["The", "Dark", "Knight"], "genre": ["Action", "Crime", "Drama"], "director": "Christopher Nolan", "actors": ["Christian Bale", "Heath Ledger", "Aaron Eckhart", "Michael Caine"], "description": "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.", "year": 2008, "runtime": "152 min", "rating": 9, "votes": 2303232, "revenue": "534,858,444", "metascore": 84, "certificate": "UA", "avatar": "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_UX67_CR0,0,67,98_AL_.jpg"}
{"index": {}}
{"code": 4, "title": "The Godfather: Part II", "title_suggest": ["The", "Godfather:", "Part", "II"], "genre": ["Crime", "Drama"], "director": "Francis Ford Coppola", "actors": ["Al Pacino", "Robert De Niro", "Robert Duvall", "Diane Keaton"], "description": "The early life and career of Vito Corleone in 1920s New York City is portrayed, while his son, Michael, expands and tightens his grip on the family crime syndicate.", "year": 1974, "runtime": "202 min", "rating": 9, "votes": 1129952, "revenue": "57,300,000", "metascore": 90, "certificate": "A", "avatar": "https://m.media-amazon.com/images/M/MV5BMWMwMGQzZTItY2JlNC00OWZiLWIyMDctNDk2ZDQ2YjRjMWQ0XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_UY98_CR1,0,67,98_AL_.jpg"}
{"index": {}}
{"code": 5, "title": "12 Angry Men", "title_suggest": ["12", "Angry", "Men"], "genre": ["Crime", "Drama"], "director": "Sidney Lumet", "actors": ["Henry Fonda", "Lee J. Cobb", "Martin Balsam", "John Fiedler"], "description": "A jury holdout attempts to prevent a miscarriage of justice by forcing his colleagues to reconsider the evidence.", "year": 1957, "runtime": "96 min", "rating": 9, "votes": 689845, "revenue": "4,360,000", "metascore": 96, "certificate": "U", "avatar": "https://m.media-amazon.com/images/M/MV5BMWU4N2FjNzYtNTVkNC00NzQ0LTg0MjAtYTJlMjFhNGUxZDFmXkEyXkFqcGdeQXVyNjc1NTYyMjg@._V1_UX67_CR0,0,67,98_AL_.jpg"}

````

## APP Search Store

For more information about APP [check here](https://github.com/andreluiz1987/app-search-store)

## More information

For more informatrion visit
post [Building Search API with ElasticSearch— Part 01: Preparing the environment](https://medium.com/@andre.luiz1987/building-search-api-with-elasticsearch-part-01-preparing-the-environment-f3c73fca06b7)

## Run Agent APM Server

````
java -javaagent:./elastic-apm-agent-1.32.0.jar \
-Delastic.apm.service_name=search-store  \
-Delastic.apm.server_urls=http://localhost:8200 \
-Delastic.apm.environment=dev \
-Delastic.apm.application_packages=com.company.searchstore \
-jar ./target/search-store-1.0.0.jar
````
