Feature: Security
  The schedule endpoints must reject unauthenticated callers.
  /api/enqueueAll is deliberately not covered here: it is unauthenticated by design and sweeps every
  task across every tenant, so invoking it out of band would double-fire live scheduled work.

  Background:
    Given the schedule application is alive

  Scenario: Ping is publicly available
    When I GET "ping" anonymously
    Then the response body is "pong"

  Scenario: Listing scheduled tasks requires authentication
    When I GET "api/schedule" anonymously
    Then the request is rejected

  Scenario: Viewing a scheduled task by id requires authentication
    When I GET "api/schedule/1" anonymously
    Then the request is rejected

  Scenario: Creating a scheduled task requires authentication
    When I POST "api/schedule" anonymously
    Then the request is rejected

  Scenario: Updating a scheduled task requires authentication
    When I PUT "api/schedule/1" anonymously
    Then the request is rejected

  Scenario: Deleting a scheduled task requires authentication
    When I DELETE "api/schedule/1" anonymously
    Then the request is rejected

  Scenario: Reaping spent tasks requires authentication
    When I DELETE "cleanup" anonymously
    Then the request is rejected
