package steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.And;


public class CaseStudy {

    BDDMethods bddMethods = new BDDMethods();

    @Given("I authenticate the server using Api key")
    public void iAuthenticateTheServerUsingApiKey() {
        bddMethods.shouldAuthenticationSuccess();
        //bddMethods.shouldAuthenticationFail();
    }

    @When("I list trending movies")
    public void iListTrendingMovies() {
        bddMethods.shouldListTrendingMoviesForAWeek();
        bddMethods.shouldListTrendingMoviesForADay();
        //bddMethods.shouldListTrendingMoviesFailWhenPageIsNotValid();
        //bddMethods.shouldListTrendingMoviesFailWhenAPIKEYWrong();
    }

    @And("I pick one randomly and get details of the movie")
    public void iPickOneRandomlyAndGetDetailsOfTheMovie() {
        bddMethods.shouldListMovieDetailsSuccess();
        //bddMethods.shouldListMovieDetailsFailWhenAPIKEYWrong();
        //bddMethods.shouldListMovieDetailsFailWhenMovieIDUnknown();
    }

    @And("I post a rating for the movie")
    public void iPostARatingForTheMovie() {
        bddMethods.shouldPostARatingForAMovieSuccess();
        //bddMethods.shouldPostARatingForAMovieNotValidSessionIDFail();
        //bddMethods.shouldPostARatingForAMovieNotValidDataFail();
        //bddMethods.shouldPostARatingForAMovieNoneValidIDFail();

    }

    @And("I delete the rating of the movie")
    public void iDeleteTheRatingOfTheMovie() {
        bddMethods.shouldDeleteARatingFromAMovieSuccess();
        //bddMethods.shouldDeleteARatingFromAMovieNotValidSessionIDFail();

    }


}
