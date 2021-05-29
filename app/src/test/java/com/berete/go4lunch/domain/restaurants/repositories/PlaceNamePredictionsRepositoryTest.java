package com.berete.go4lunch.domain.restaurants.repositories;

import com.berete.go4lunch.domain.restaurants.services.AutocompleteService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.berete.go4lunch.FakeData.fakeFilter;
import static com.berete.go4lunch.FakeData.fakeGeoCoordinates;
import static com.berete.go4lunch.FakeData.fakeAutocompleteInput;
import static com.berete.go4lunch.FakeData.fakeLangCode;
import static com.berete.go4lunch.FakeData.fakePredictionCallback;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class PlaceNamePredictionsRepositoryTest {

  AutocompleteService autocompleteService;
  PlaceNamePredictionsRepository predictionsRepository;

  @Before
  public void setUp() {
    autocompleteService = mock(AutocompleteService.class);
    predictionsRepository = new PlaceNamePredictionsRepository(autocompleteService);
  }

  @Test
  public void should_predict_without_filter() {
    predictionsRepository.subscribeForResults(fakePredictionCallback);
    predictionsRepository.predict(fakeAutocompleteInput, fakeGeoCoordinates, fakeLangCode);
    verify(autocompleteService)
        .predict(
            fakeAutocompleteInput,
            fakeGeoCoordinates,
            fakeLangCode,
            AutocompleteService.DEFAULT_COVERED_RADIUS,
            fakePredictionCallback);
  }

  @Test
  public void prediction_without_filter_should_fail_if_their_is_no_subscriber() {
    Assert.assertThrows(
        AssertionError.class,
        () -> predictionsRepository.predict(fakeAutocompleteInput, fakeGeoCoordinates, fakeLangCode));
  }
  @Test
  public void should_predict_with_filter() {
    predictionsRepository.subscribeForFilteredResult(fakePredictionCallback);
    predictionsRepository.predictWithFilter(fakeAutocompleteInput, fakeGeoCoordinates, fakeLangCode, fakeFilter);
    verify(autocompleteService)
        .predictWithFilter(
            fakeAutocompleteInput,
            fakeGeoCoordinates,
            fakeLangCode,
            AutocompleteService.DEFAULT_COVERED_RADIUS,
            fakeFilter,
            fakePredictionCallback);
  }

  @Test
  public void prediction_with_filter_should_fail_if_their_is_no_subscriber() {
    Assert.assertThrows(
        AssertionError.class,
        () -> predictionsRepository.predictWithFilter(fakeAutocompleteInput, fakeGeoCoordinates,fakeLangCode, fakeFilter));
  }

}
