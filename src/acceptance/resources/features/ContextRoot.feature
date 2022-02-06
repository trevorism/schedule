Feature: Context Root of this API
  In order to use the Testing API, it must be available

  Scenario: ContextRoot https
    Given the schedule application is alive
    When I navigate to https://schedule.datastore.trevorism.com/
    Then the API returns a link to the help page

  Scenario: Ping https
    Given the schedule application is alive
    When I navigate to /ping on https://schedule.datastore.trevorism.com/
    Then pong is returned, to indicate the service is alive
