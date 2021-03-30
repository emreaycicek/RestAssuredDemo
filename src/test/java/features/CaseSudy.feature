Feature:
  Case Study Sample Project

  Scenario: List trending movies, pick one randomly, get details of the movie, post a rating for the movie, delete the rating of the movie
    Given I authenticate the server using Api key
    When I list trending movies
    And I pick one randomly and get details of the movie
    And I post a rating for the movie
    And I delete the rating of the movie